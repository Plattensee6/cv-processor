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
public class ProfileValidator {

    /**
     * Validate profile
     * 
     * @param extractedFields the extracted fields
     * @return validation result
     */
    public ValidationResult validate(ExtractedFields extractedFields) {
        log.debug("Validating profile");

        String profile = extractedFields.getProfile();
        
        if (profile == null || profile.trim().isEmpty()) {
            return ValidationResult.invalid("Profile not found");
        }

        String lowerCaseProfile = profile.toLowerCase();
        
        boolean hasGenAI = containsInterest(lowerCaseProfile, "genai") || 
                          containsInterest(lowerCaseProfile, "generative ai") ||
                          containsInterest(lowerCaseProfile, "generative artificial intelligence");
        boolean hasJava = containsInterest(lowerCaseProfile, "java");

        if (!hasGenAI) {
            return ValidationResult.invalid("Profile must include interest in GenAI or Generative AI");
        }

        if (!hasJava) {
            return ValidationResult.invalid("Profile must include interest in Java");
        }

        return ValidationResult.valid("Profile includes required interest in GenAI and Java");
    }

    /**
     * Check if profile contains a specific interest
     */
    private boolean containsInterest(String profile, String interest) {
        return profile.contains(interest);
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult valid(String message) {
            return new ValidationResult(true, message);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
