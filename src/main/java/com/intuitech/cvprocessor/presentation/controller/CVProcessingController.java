package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.application.service.CVProcessingService;
import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for CV processing operations
 * 
 * Handles CV processing requests and provides endpoints for field extraction.
 */
@RestController
@RequestMapping("/api/cv/process")
@RequiredArgsConstructor
@Slf4j
public class CVProcessingController {

    private final CVProcessingService cvProcessingService;

    /**
     * Process CV document and extract fields
     * 
     * @param requestId the processing request ID
     * @return processing result
     */
    @PostMapping("/{requestId}")
    public ResponseEntity<?> processCV(@PathVariable Long requestId) {
        log.info("Received CV processing request for ID: {}", requestId);

        try {
            CVProcessingRequest request = cvProcessingService.processCV(requestId);
            
            Map<String, Object> response = Map.of(
                    "requestId", request.getId(),
                    "status", request.getStatus(),
                    "message", "CV processed successfully",
                    "processedAt", request.getUpdatedAt()
            );

            return ResponseEntity.ok(response);

        } catch (CVProcessingService.CVProcessingException e) {
            log.error("CV processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "CV processing failed",
                            "message", e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        } catch (Exception e) {
            log.error("Unexpected error during CV processing: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "error", "Internal server error",
                            "message", "An unexpected error occurred",
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        }
    }

    /**
     * Get extracted fields for a processing request
     * 
     * @param requestId the request ID
     * @return extracted fields
     */
    @GetMapping("/{requestId}/fields")
    public ResponseEntity<?> getExtractedFields(@PathVariable Long requestId) {
        log.debug("Retrieving extracted fields for request ID: {}", requestId);

        try {
            CVProcessingRequest request = cvProcessingService.getProcessingRequestWithFields(requestId);
            
            // Find extracted fields for this request
            ExtractedFields extractedFields = request.getExtractedFields();
            
            if (extractedFields == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = Map.of(
                    "requestId", request.getId(),
                    "status", request.getStatus(),
                    "extractedFields", Map.of(
                            "workExperienceYears", extractedFields.getWorkExperienceYears() != null ? 
                                    extractedFields.getWorkExperienceYears() : "null",
                            "workExperienceDetails", extractedFields.getWorkExperienceDetails() != null ? 
                                    extractedFields.getWorkExperienceDetails() : "null",
                            "skills", extractedFields.getSkills() != null ? 
                                    extractedFields.getSkills() : "null",
                            "languages", extractedFields.getLanguages() != null ? 
                                    extractedFields.getLanguages() : "null",
                            "profile", extractedFields.getProfile() != null ? 
                                    extractedFields.getProfile() : "null"
                    ),
                    "extractedAt", extractedFields.getCreatedAt()
            );

            return ResponseEntity.ok(response);

        } catch (CVProcessingService.CVProcessingException e) {
            log.error("Failed to retrieve extracted fields: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Unexpected error retrieving extracted fields: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "error", "Internal server error",
                            "message", "An unexpected error occurred",
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        }
    }

    /**
     * Health check for CV processing service
     * 
     * @return service health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("CV processing service health check requested");

        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "CV Processing Service",
                "timestamp", java.time.LocalDateTime.now(),
                "endpoints", Map.of(
                        "process", "POST /api/cv/process/{requestId}",
                        "fields", "GET /api/cv/process/{requestId}/fields"
                )
        );

        return ResponseEntity.ok(health);
    }
}
