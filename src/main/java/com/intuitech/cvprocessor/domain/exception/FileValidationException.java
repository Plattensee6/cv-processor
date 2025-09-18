package com.intuitech.cvprocessor.domain.exception;

/**
 * Exception for file validation errors
 * 
 * Thrown when file validation fails (invalid type, size, etc.)
 */
public class FileValidationException extends CVProcessingException {

    public FileValidationException(String message) {
        super("FILE_VALIDATION_ERROR", message);
    }

    public FileValidationException(String message, Throwable cause) {
        super("FILE_VALIDATION_ERROR", message, cause);
    }

    public FileValidationException(String message, Object... parameters) {
        super("FILE_VALIDATION_ERROR", message, parameters);
    }

    public FileValidationException(String message, Throwable cause, Object... parameters) {
        super("FILE_VALIDATION_ERROR", message, cause, parameters);
    }
}
