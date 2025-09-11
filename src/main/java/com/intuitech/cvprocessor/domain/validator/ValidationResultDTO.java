package com.intuitech.cvprocessor.domain.validator;

/**
 * Common validation result for all validators
 * 
 * Represents the result of a single field validation.
 */
public class ValidationResultDTO {
    private final boolean valid;
    private final String message;
    private final String fieldName;

    private ValidationResultDTO(boolean valid, String message, String fieldName) {
        this.valid = valid;
        this.message = message;
        this.fieldName = fieldName;
    }

    public static ValidationResultDTO valid(String message, String fieldName) {
        return new ValidationResultDTO(true, message, fieldName);
    }

    public static ValidationResultDTO invalid(String message, String fieldName) {
        return new ValidationResultDTO(false, message, fieldName);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public String getFieldName() {
        return fieldName;
    }
}
