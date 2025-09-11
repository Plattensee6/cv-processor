package com.intuitech.cvprocessor.domain.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for profile field
 * 
 * Validates that profile includes interest in GenAI and Java.
 */
@Component
@Slf4j
public class ProfileValidator implements Validator {
    
    private static final String FIELD_NAME = "profile";

    @Override
    public ValidationResultDTO validate(ExtractedFields extractedFields) {
        log.debug("Validating profile");

        String profile = extractedFields.getProfile();
        
        if (profile == null || profile.trim().isEmpty()) {
            return ValidationResultDTO.invalid("Profile not found", FIELD_NAME);
        }

        String lowerCaseProfile = profile.toLowerCase();
        
        boolean hasGenAI = containsInterest(lowerCaseProfile, "genai") || 
                          containsInterest(lowerCaseProfile, "generative ai") ||
                          containsInterest(lowerCaseProfile, "generative artificial intelligence");
        boolean hasJava = containsInterest(lowerCaseProfile, "java");

        if (!hasGenAI) {
            return ValidationResultDTO.invalid("Profile must include interest in GenAI or Generative AI", FIELD_NAME);
        }

        if (!hasJava) {
            return ValidationResultDTO.invalid("Profile must include interest in Java", FIELD_NAME);
        }

        return ValidationResultDTO.valid("Profile includes required interest in GenAI and Java", FIELD_NAME);
    }

    /**
     * Check if profile contains a specific interest
     */
    private boolean containsInterest(String profile, String interest) {
        return profile.contains(interest);
    }

    @Override
    public String getFieldName() {
        return FIELD_NAME;
    }

    @Override
    public int getPriority() {
        return 40; // Lower priority
    }
}
