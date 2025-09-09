package com.intuitech.cvprocessor.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.config.HuggingFaceConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service for extracting CV fields using Hugging Face open-source models
 * 
 * Handles LLM integration for field extraction with resilience patterns.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HuggingFaceFieldExtractor {

    private final RestTemplate restTemplate;
    private final HuggingFaceConfig huggingFaceConfig;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Extract fields from CV document text
     * 
     * @param documentText the CV document text
     * @return extracted fields
     * @throws FieldExtractionException if extraction fails
     */
    @Retry(name = "huggingface-extraction")
    @CircuitBreaker(name = "huggingface-extraction")
    public ExtractedFields extractFields(String documentText) throws FieldExtractionException {
        log.info("Starting field extraction for document length: {}", documentText.length());

        try {
            // Build prompt
            String prompt = promptBuilder.buildFieldExtractionPrompt(documentText);

            // Create request payload
            Map<String, Object> requestPayload = Map.of(
                    "inputs", prompt,
                    "parameters", Map.of(
                            "max_new_tokens", huggingFaceConfig.getMaxTokens(),
                            "temperature", huggingFaceConfig.getTemperature(),
                            "return_full_text", false,
                            "do_sample", true
                    )
            );

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(huggingFaceConfig.getApiKey());

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

            // Call Hugging Face API using RestTemplate
            log.debug("Calling Hugging Face API with model: {}", huggingFaceConfig.getModel());
            
            String url = "https://api-inference.huggingface.co/models/" + huggingFaceConfig.getModel();
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new FieldExtractionException("Empty response from Hugging Face API");
            }

            log.debug("Received Hugging Face response: {}", responseBody);

            // Parse response
            ExtractedFields extractedFields = parseExtractionResponse(responseBody);

            log.info("Successfully extracted fields from CV");
            return extractedFields;

        } catch (Exception e) {
            log.error("Field extraction failed: {}", e.getMessage(), e);
            throw new FieldExtractionException("Failed to extract fields from CV", e);
        }
    }

    /**
     * Parse Hugging Face response into ExtractedFields object
     */
    private ExtractedFields parseExtractionResponse(String response) throws FieldExtractionException {
        try {
            // Clean the response - remove any extra text before/after JSON
            String cleanedResponse = cleanJsonResponse(response);
            
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);

            ExtractedFields.ExtractedFieldsBuilder builder = ExtractedFields.builder();

            // Parse work experience
            if (jsonNode.has("workExperience")) {
                JsonNode workExp = jsonNode.get("workExperience");
                if (workExp.has("years") && !workExp.get("years").isNull()) {
                    builder.workExperienceYears(workExp.get("years").asInt());
                }
                if (workExp.has("details") && !workExp.get("details").isNull()) {
                    builder.workExperienceDetails(workExp.get("details").asText());
                }
            }

            // Parse skills
            if (jsonNode.has("skills") && !jsonNode.get("skills").isNull()) {
                if (jsonNode.get("skills").isArray()) {
                    StringBuilder skillsBuilder = new StringBuilder();
                    for (JsonNode skill : jsonNode.get("skills")) {
                        if (skillsBuilder.length() > 0) {
                            skillsBuilder.append(", ");
                        }
                        skillsBuilder.append(skill.asText());
                    }
                    builder.skills(skillsBuilder.toString());
                } else {
                    builder.skills(jsonNode.get("skills").asText());
                }
            }

            // Parse languages
            if (jsonNode.has("languages") && !jsonNode.get("languages").isNull()) {
                if (jsonNode.get("languages").isArray()) {
                    StringBuilder languagesBuilder = new StringBuilder();
                    for (JsonNode language : jsonNode.get("languages")) {
                        if (languagesBuilder.length() > 0) {
                            languagesBuilder.append(", ");
                        }
                        languagesBuilder.append(language.asText());
                    }
                    builder.languages(languagesBuilder.toString());
                } else {
                    builder.languages(jsonNode.get("languages").asText());
                }
            }

            // Parse profile
            if (jsonNode.has("profile") && !jsonNode.get("profile").isNull()) {
                builder.profile(jsonNode.get("profile").asText());
            }

            return builder.build();

        } catch (Exception e) {
            log.error("Failed to parse Hugging Face response: {}", e.getMessage());
            throw new FieldExtractionException("Failed to parse extraction response", e);
        }
    }

    /**
     * Clean the response to extract valid JSON
     */
    private String cleanJsonResponse(String response) {
        // Find the first { and last } to extract JSON
        int startIndex = response.indexOf('{');
        int lastIndex = response.lastIndexOf('}');
        
        if (startIndex != -1 && lastIndex != -1 && lastIndex > startIndex) {
            return response.substring(startIndex, lastIndex + 1);
        }
        
        return response;
    }

    /**
     * Custom exception for field extraction errors
     */
    public static class FieldExtractionException extends Exception {
        public FieldExtractionException(String message) {
            super(message);
        }

        public FieldExtractionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}