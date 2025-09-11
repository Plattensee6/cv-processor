package com.intuitech.cvprocessor.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuitech.cvprocessor.application.service.FieldExtractor;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
            // Build prompt
            String prompt = promptBuilder.buildFieldExtractionPrompt(documentText);

            // Create request payload for Ollama
            Map<String, Object> requestPayload = Map.of(
                    "model", ollamaConfig.getModel(),
                    "prompt", prompt,
                    "stream", false,
                    "options", Map.of(
                            "temperature", 0.1,
                            "top_p", 0.9,
                            "max_tokens", 2000
                    )
            );

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Call Ollama API
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

            // Parse response
            ExtractedFields extractedFields = parseOllamaResponse(responseBody);

            // Record success metrics
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            ollamaMetrics.recordSuccess();
            ollamaMetrics.recordResponseTime(responseTime);

            log.info("Successfully extracted fields from CV using Ollama in {}ms", responseTime);
            return extractedFields;

        } catch (Exception e) {
            // Record failure metrics
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
            
            // Ollama returns the generated text in the "response" field
            String generatedText = jsonNode.get("response").asText();
            
            // Clean and parse the generated JSON
            String cleanedResponse = cleanJsonResponse(generatedText);
            JsonNode extractedJson = objectMapper.readTree(cleanedResponse);

            ExtractedFields.ExtractedFieldsBuilder builder = ExtractedFields.builder();

            // Parse work experience
            if (extractedJson.has("workExperience")) {
                JsonNode workExp = extractedJson.get("workExperience");
                if (workExp.has("years") && !workExp.get("years").isNull()) {
                    builder.workExperienceYears(workExp.get("years").asInt());
                }
                if (workExp.has("details") && !workExp.get("details").isNull()) {
                    builder.workExperienceDetails(workExp.get("details").asText());
                }
            }

            // Parse skills
            if (extractedJson.has("skills") && !extractedJson.get("skills").isNull()) {
                if (extractedJson.get("skills").isArray()) {
                    StringBuilder skillsBuilder = new StringBuilder();
                    for (JsonNode skill : extractedJson.get("skills")) {
                        if (skillsBuilder.length() > 0) {
                            skillsBuilder.append(", ");
                        }
                        skillsBuilder.append(skill.asText());
                    }
                    builder.skills(skillsBuilder.toString());
                } else {
                    builder.skills(extractedJson.get("skills").asText());
                }
            }

            // Parse languages
            if (extractedJson.has("languages") && !extractedJson.get("languages").isNull()) {
                if (extractedJson.get("languages").isArray()) {
                    StringBuilder languagesBuilder = new StringBuilder();
                    for (JsonNode language : extractedJson.get("languages")) {
                        if (languagesBuilder.length() > 0) {
                            languagesBuilder.append(", ");
                        }
                        languagesBuilder.append(language.asText());
                    }
                    builder.languages(languagesBuilder.toString());
                } else {
                    builder.languages(extractedJson.get("languages").asText());
                }
            }

            // Parse profile
            if (extractedJson.has("profile") && !extractedJson.get("profile").isNull()) {
                builder.profile(extractedJson.get("profile").asText());
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
            // Simple health check - try to get model list
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
