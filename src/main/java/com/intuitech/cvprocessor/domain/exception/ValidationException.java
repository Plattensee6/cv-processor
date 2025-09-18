package com.intuitech.cvprocessor.domain.exception;

/**
 * Exception for validation errors
 * 
 * Thrown when field validation fails (invalid data, missing required fields, etc.)
 */
public class ValidationException extends CVProcessingException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
    }

    public ValidationException(String message, Object... parameters) {
        super("VALIDATION_ERROR", message, parameters);
    }

    public ValidationException(String message, Throwable cause, Object... parameters) {
        super("VALIDATION_ERROR", message, cause, parameters);
    }
}
