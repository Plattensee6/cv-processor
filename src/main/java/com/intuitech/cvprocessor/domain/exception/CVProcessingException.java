package com.intuitech.cvprocessor.domain.exception;

/**
 * Base exception for CV processing operations
 * 
 * This is the root exception for all CV processing related errors.
 */
public class CVProcessingException extends Exception {

    private final String errorCode;
    private final Object[] parameters;

    public CVProcessingException(String message) {
        super(message);
        this.errorCode = null;
        this.parameters = null;
    }

    public CVProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.parameters = null;
    }

    public CVProcessingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = null;
    }

    public CVProcessingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = null;
    }

    public CVProcessingException(String errorCode, String message, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public CVProcessingException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        if (errorCode != null) {
            return String.format("[%s] %s", errorCode, getMessage());
        }
        return getMessage();
    }
}
