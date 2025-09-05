package com.intuitech.cvprocessor.domain.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for languages field
 * 
 * Validates that languages include both Hungarian and English.
 */
@Component
@Slf4j
public class LanguagesValidator {

    /**
     * Validate languages
     * 
     * @param extractedFields the extracted fields
     * @return validation result
     */
    public ValidationResult validate(ExtractedFields extractedFields) {
        log.debug("Validating languages");

        String languages = extractedFields.getLanguages();
        
        if (languages == null || languages.trim().isEmpty()) {
            return ValidationResult.invalid("Languages not found");
        }

        String lowerCaseLanguages = languages.toLowerCase();
        
        boolean hasHungarian = containsLanguage(lowerCaseLanguages, "hungarian") || 
                              containsLanguage(lowerCaseLanguages, "magyar");
        boolean hasEnglish = containsLanguage(lowerCaseLanguages, "english") || 
                            containsLanguage(lowerCaseLanguages, "angol");

        if (!hasHungarian) {
            return ValidationResult.invalid("Languages must include Hungarian");
        }

        if (!hasEnglish) {
            return ValidationResult.invalid("Languages must include English");
        }

        return ValidationResult.valid("Languages include required Hungarian and English");
    }

    /**
     * Check if languages contain a specific language
     */
    private boolean containsLanguage(String languages, String language) {
        return languages.contains(language);
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
