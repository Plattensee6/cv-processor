package com.intuitech.cvprocessor.domain.exception;

/**
 * Exception for resource not found errors
 * 
 * Thrown when a requested resource (processing request, extracted fields, etc.) is not found.
 */
public class ResourceNotFoundException extends CVProcessingException {

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super("RESOURCE_NOT_FOUND", message, cause);
    }

    public ResourceNotFoundException(String message, Object... parameters) {
        super("RESOURCE_NOT_FOUND", message, parameters);
    }

    public ResourceNotFoundException(String message, Throwable cause, Object... parameters) {
        super("RESOURCE_NOT_FOUND", message, cause, parameters);
    }
}
