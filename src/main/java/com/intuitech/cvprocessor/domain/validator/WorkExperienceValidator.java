package com.intuitech.cvprocessor.domain.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for work experience field
 * 
 * Validates that work experience is between 0 and 2 years.
 */
@Component
@Slf4j
public class WorkExperienceValidator {

    private static final int MIN_YEARS = 0;
    private static final int MAX_YEARS = 2;

    /**
     * Validate work experience
     * 
     * @param extractedFields the extracted fields
     * @return validation result
     */
    public ValidationResult validate(ExtractedFields extractedFields) {
        log.debug("Validating work experience");

        Integer years = extractedFields.getWorkExperienceYears();
        
        if (years == null) {
            return ValidationResult.invalid("Work experience years not found");
        }

        if (years < MIN_YEARS || years > MAX_YEARS) {
            return ValidationResult.invalid(
                String.format("Work experience %d years is not between %d and %d years", 
                    years, MIN_YEARS, MAX_YEARS)
            );
        }

        return ValidationResult.valid(
            String.format("Work experience %d years is within valid range (%d-%d years)", 
                years, MIN_YEARS, MAX_YEARS)
        );
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
