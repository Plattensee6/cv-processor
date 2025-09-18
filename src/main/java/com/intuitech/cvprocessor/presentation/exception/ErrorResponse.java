package com.intuitech.cvprocessor.presentation.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response structure
 * 
 * Provides consistent error response format across the application.
 */
@Data
@Builder
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private Map<String, String> fieldErrors;
    private String traceId;
    
    /**
     * Create a simple error response
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }
    
    /**
     * Create an error response with error code
     */
    public static ErrorResponse of(int status, String error, String message, String errorCode, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .build();
    }
    
    /**
     * Create an error response with field errors
     */
    public static ErrorResponse of(int status, String error, String message, String errorCode, 
                                 String path, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .fieldErrors(fieldErrors)
                .build();
    }
}
