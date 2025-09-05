package com.intuitech.cvprocessor.infrastructure.service;

import ai.huggingface.HuggingFaceApi;
import ai.huggingface.types.TextGenerationRequest;
import ai.huggingface.types.TextGenerationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.config.HuggingFaceConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private final HuggingFaceApi huggingFaceApi;
    private final HuggingFaceConfig huggingFaceConfig;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

            // Create text generation request
            TextGenerationRequest request = TextGenerationRequest.builder()
                    .model(huggingFaceConfig.getModel())
                    .inputs(prompt)
                    .parameters(Map.of(
                            "max_new_tokens", huggingFaceConfig.getMaxTokens(),
                            "temperature", huggingFaceConfig.getTemperature(),
                            "return_full_text", false,
                            "do_sample", true
                    ))
                    .build();

            // Call Hugging Face API
            log.debug("Calling Hugging Face API with model: {}", huggingFaceConfig.getModel());
            TextGenerationResponse response = huggingFaceApi.textGeneration(request);

            if (response == null || response.generatedText() == null || response.generatedText().isEmpty()) {
                throw new FieldExtractionException("Empty response from Hugging Face API");
            }

            String responseText = response.generatedText().get(0);
            log.debug("Received Hugging Face response: {}", responseText);

            // Parse response
            ExtractedFields extractedFields = parseExtractionResponse(responseText);

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
                List<String> skills = objectMapper.convertValue(jsonNode.get("skills"), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                builder.skills(String.join(", ", skills));
            }

            // Parse languages
            if (jsonNode.has("languages") && !jsonNode.get("languages").isNull()) {
                List<String> languages = objectMapper.convertValue(jsonNode.get("languages"), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                builder.languages(String.join(", ", languages));
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
