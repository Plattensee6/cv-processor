package com.intuitech.cvprocessor.domain.exception;

/**
 * Exception for document parsing errors
 * 
 * Thrown when document parsing fails (corrupted file, unsupported format, etc.)
 */
public class DocumentParsingException extends CVProcessingException {

    public DocumentParsingException(String message) {
        super("DOCUMENT_PARSING_ERROR", message);
    }

    public DocumentParsingException(String message, Throwable cause) {
        super("DOCUMENT_PARSING_ERROR", message, cause);
    }

    public DocumentParsingException(String message, Object... parameters) {
        super("DOCUMENT_PARSING_ERROR", message, parameters);
    }

    public DocumentParsingException(String message, Throwable cause, Object... parameters) {
        super("DOCUMENT_PARSING_ERROR", message, cause, parameters);
    }
}
