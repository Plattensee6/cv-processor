package com.intuitech.cvprocessor.domain.exception;

/**
 * Exception for field extraction errors
 * 
 * Thrown when field extraction from CV fails (LLM service unavailable, parsing error, etc.)
 */
public class FieldExtractionException extends CVProcessingException {

    public FieldExtractionException(String message) {
        super("FIELD_EXTRACTION_ERROR", message);
    }

    public FieldExtractionException(String message, Throwable cause) {
        super("FIELD_EXTRACTION_ERROR", message, cause);
    }

    public FieldExtractionException(String message, Object... parameters) {
        super("FIELD_EXTRACTION_ERROR", message, parameters);
    }

    public FieldExtractionException(String message, Throwable cause, Object... parameters) {
        super("FIELD_EXTRACTION_ERROR", message, cause, parameters);
    }
}
