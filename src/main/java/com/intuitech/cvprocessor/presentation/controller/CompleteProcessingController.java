package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.application.dto.ProcessingResponseDTO;
import com.intuitech.cvprocessor.application.service.CompleteProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for complete CV processing operations
 * 
 * Handles end-to-end CV processing including field extraction and validation.
 */
@RestController
@RequestMapping("/api/cv/complete")
@RequiredArgsConstructor
@Slf4j
public class CompleteProcessingController {

    private final CompleteProcessingService completeProcessingService;

    /**
     * Process CV completely - extract fields and validate
     * 
     * @param requestId the processing request ID
     * @return complete processing result
     */
    @PostMapping("/{requestId}")
    public ResponseEntity<?> processCVCompletely(@PathVariable Long requestId) {
        log.info("Received complete CV processing request for ID: {}", requestId);

        try {
            ProcessingResponseDTO response = completeProcessingService.processCVCompletely(requestId);
            return ResponseEntity.ok(response);

        } catch (CompleteProcessingService.CompleteProcessingException e) {
            log.error("Complete CV processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Complete CV processing failed",
                            "message", e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error("Unexpected error during complete CV processing: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "error", "Internal server error",
                            "message", "An unexpected error occurred",
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        }
    }

    /**
     * Get complete processing result
     * 
     * @param requestId the request ID
     * @return complete processing result
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<?> getCompleteProcessingResult(@PathVariable Long requestId) {
        log.debug("Retrieving complete processing result for request ID: {}", requestId);

        try {
            ProcessingResponseDTO response = completeProcessingService.getCompleteProcessingResult(requestId);
            return ResponseEntity.ok(response);

        } catch (CompleteProcessingService.CompleteProcessingException e) {
            log.error("Failed to retrieve complete processing result: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Unexpected error retrieving complete processing result: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "error", "Internal server error",
                            "message", "An unexpected error occurred",
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        }
    }

    /**
     * Health check for complete processing service
     * 
     * @return service health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Complete processing service health check requested");

        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "Complete Processing Service",
                "timestamp", java.time.LocalDateTime.now(),
                "endpoints", Map.of(
                        "process", "POST /api/cv/complete/{requestId}",
                        "result", "GET /api/cv/complete/{requestId}"
                )
        );

        return ResponseEntity.ok(health);
    }
}
