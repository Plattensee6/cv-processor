package com.intuitech.cvprocessor.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for building prompts for LLM field extraction
 * 
 * Creates structured prompts for extracting specific fields from CV documents.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromptBuilder {

    /**
     * Build prompt for extracting CV fields
     * 
     * @param documentText the CV document text
     * @return structured prompt for field extraction
     */
    public String buildFieldExtractionPrompt(String documentText) {
        log.debug("Building field extraction prompt for document length: {}", documentText.length());

        return String.format("""
            You are an expert CV analyst. Extract the following information from the CV document below and return it in JSON format.
            
            Required fields to extract:
            1. Work Experience: Number of years of work experience (as integer)
            2. Skills: List of skills mentioned in the CV (as array of strings)
            3. Languages: List of languages mentioned in the CV (as array of strings)
            4. Profile: The profile/summary section text (as string)
            
            CV Document:
            %s
            
            Return the result in this exact JSON format:
            {
                "workExperience": {
                    "years": <integer>,
                    "details": "<string>"
                },
                "skills": ["<skill1>", "<skill2>", ...],
                "languages": ["<language1>", "<language2>", ...],
                "profile": "<profile text>"
            }
            
            Important:
            - For work experience, count only full-time professional work experience
            - Include all technical and soft skills mentioned
            - Include all languages with proficiency levels if mentioned
            - Extract the complete profile/summary section
            - If a field is not found, use null for that field
            - Return only valid JSON, no additional text
            """, documentText);
    }

    /**
     * Build prompt for validating extracted fields
     * 
     * @param extractedFields the extracted fields JSON
     * @return prompt for validation
     */
    public String buildValidationPrompt(String extractedFields) {
        log.debug("Building validation prompt for extracted fields");

        return String.format("""
            You are a CV validation expert. Review the extracted CV fields below and validate them against these business rules:
            
            Validation Rules:
            1. Work Experience: Must be between 0 and 2 years
            2. Skills: Must include both "Java" and "LLM" (or "Large Language Model" or "AI")
            3. Languages: Must include both "Hungarian" and "English"
            4. Profile: Must mention interest in "GenAI" (or "Generative AI") and "Java"
            
            Extracted Fields:
            %s
            
            Return validation result in this exact JSON format:
            {
                "workExperienceValid": <boolean>,
                "workExperienceMessage": "<validation message>",
                "skillsValid": <boolean>,
                "skillsMessage": "<validation message>",
                "languagesValid": <boolean>,
                "languagesMessage": "<validation message>",
                "profileValid": <boolean>,
                "profileMessage": "<validation message>",
                "overallValid": <boolean>
            }
            
            Important:
            - Be strict but fair in validation
            - Provide clear messages explaining why validation failed
            - overallValid should be true only if ALL validations pass
            - Return only valid JSON, no additional text
            """, extractedFields);
    }
}
