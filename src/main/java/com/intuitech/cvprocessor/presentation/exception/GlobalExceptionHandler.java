package com.intuitech.cvprocessor.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler for the application
 * 
 * Handles exceptions across all controllers and provides consistent error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle file upload size exceeded exceptions
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error("File upload size exceeded: {}", e.getMessage());
        
        Map<String, Object> error = Map.of(
                "error", "File size exceeded",
                "message", "The uploaded file exceeds the maximum allowed size",
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.PAYLOAD_TOO_LARGE.value()
        );
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e.getMessage());
        
        Map<String, Object> error = Map.of(
                "error", "Invalid argument",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        
        Map<String, Object> error = Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred",
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
