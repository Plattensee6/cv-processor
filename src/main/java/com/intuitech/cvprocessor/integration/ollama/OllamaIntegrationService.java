package com.intuitech.cvprocessor.integration.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuitech.cvprocessor.feature.cvprocessing.FieldExtractor;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import com.intuitech.cvprocessor.infrastructure.service.PromptBuilder;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Ollama integration service for LLM-based field extraction
 * 
 * Handles integration with Ollama local models for CV field extraction.
 * Implements the FieldExtractor interface to provide loose coupling.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaIntegrationService implements FieldExtractor {

    private final RestTemplate restTemplate;
    private final OllamaConfig ollamaConfig;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;
    private final OllamaMetrics ollamaMetrics;
    private final List<JsonFieldParser> jsonFieldParsers;

    /**
     * Extract fields from CV document text using Ollama
     * 
     * @param documentText the CV document text
     * @return extracted fields
     * @throws FieldExtractionException if extraction fails
     */
    @Retry(name = "ollama-extraction")
    @CircuitBreaker(name = "ollama-extraction")
    public ExtractedFields extractFields(String documentText) throws FieldExtractionException {
        long startTime = System.currentTimeMillis();
        ollamaMetrics.incrementConcurrentRequests();
        
        try {
            String prompt = promptBuilder.buildFieldExtractionPrompt(documentText);

            Map<String, Object> requestPayload = Map.of(
                    "model", ollamaConfig.getModel(),
                    "prompt", prompt,
                    "stream", false,
                    "options", Map.of(
                            "temperature", ollamaConfig.getTemperature(),
                            "top_p", ollamaConfig.getTopP(),
                            "max_tokens", ollamaConfig.getMaxTokens()
                    )
            );

            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

        
            log.debug("Calling Ollama API with model: {} at {}", ollamaConfig.getModel(), ollamaConfig.getModelUrl());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    ollamaConfig.getModelUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new FieldExtractionException("Empty response from Ollama API");
            }

            log.debug("Received Ollama response: {}", responseBody);

            ExtractedFields extractedFields = parseOllamaResponse(responseBody);

            // Record success metrics
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            ollamaMetrics.recordSuccess();
            ollamaMetrics.recordResponseTime(responseTime);

            log.info("Successfully extracted fields from CV using Ollama in {}ms", responseTime);
            return extractedFields;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            ollamaMetrics.recordFailure();
            ollamaMetrics.recordResponseTime(responseTime);
            
            log.error("Field extraction failed with Ollama: {}", e.getMessage(), e);
            throw new FieldExtractionException("Failed to extract fields from CV using Ollama", e);
        } finally {
            ollamaMetrics.decrementConcurrentRequests();
        }
    }

    /**
     * Parse Ollama response into ExtractedFields object
     */
    private ExtractedFields parseOllamaResponse(String response) throws FieldExtractionException {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            String generatedText = jsonNode.get("response").asText();
            String cleanedResponse = cleanJsonResponse(generatedText);
            JsonNode extractedJson = objectMapper.readTree(cleanedResponse);

            ExtractedFields.ExtractedFieldsBuilder builder = ExtractedFields.builder();

            // Delegate field-specific parsing to strategy implementations
            for (JsonFieldParser parser : jsonFieldParsers) {
                parser.parse(extractedJson, builder);
            }

            return builder.build();

        } catch (Exception e) {
            log.error("Failed to parse Ollama response: {}", response, e);
            throw new FieldExtractionException("Failed to parse Ollama response", e);
        }
    }

    /**
     * Clean JSON response by removing extra text
     */
    private String cleanJsonResponse(String response) {
        // Remove any text before the first {
        int startIndex = response.indexOf('{');
        if (startIndex == -1) {
            throw new IllegalArgumentException("No JSON object found in response");
        }
        
        // Find the last }
        int lastIndex = response.lastIndexOf('}');
        if (lastIndex == -1) {
            throw new IllegalArgumentException("No closing brace found in response");
        }
        
        return response.substring(startIndex, lastIndex + 1);
    }

    @Override
    public boolean isAvailable() {
        try {
            String url = "http://" + ollamaConfig.getHost() + ":" + ollamaConfig.getPort() + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.debug("Ollama service not available: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getExtractorName() {
        return "Ollama";
    }
}
