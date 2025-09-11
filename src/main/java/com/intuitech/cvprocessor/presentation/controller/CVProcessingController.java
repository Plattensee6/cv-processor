package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.application.service.CVProcessingService;
import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "CV Processing", description = "CV processing and field extraction endpoints")
public class CVProcessingController {

    private final CVProcessingService cvProcessingService;

    /**
     * Process CV document and extract fields
     * 
     * @param requestId the processing request ID
     * @return processing result
     */
    @Operation(
            summary = "Process CV document",
            description = "Process a previously uploaded CV document to extract fields using LLM and validate the extracted data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "CV processed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                                "requestId": 1,
                                                "status": "COMPLETED",
                                                "message": "CV processed successfully",
                                                "processedAt": "2024-01-15T10:35:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Processing failed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Error Response",
                                    value = """
                                            {
                                                "error": "CV processing failed",
                                                "message": "Processing request not found",
                                                "timestamp": "2024-01-15T10:35:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                                            {
                                                "error": "Internal server error",
                                                "message": "An unexpected error occurred",
                                                "timestamp": "2024-01-15T10:35:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{requestId}")
    public ResponseEntity<?> processCV(
            @Parameter(
                    description = "Unique identifier of the processing request",
                    required = true,
                    example = "1"
            )
            @PathVariable Long requestId) {
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
    @Operation(
            summary = "Get extracted fields",
            description = "Retrieve the extracted fields from a processed CV document."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Extracted fields retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                                "requestId": 1,
                                                "status": "COMPLETED",
                                                "extractedFields": {
                                                    "workExperienceYears": 2,
                                                    "workExperienceDetails": "Software Engineer at TechCorp",
                                                    "skills": "Java, Spring Boot, LLM, AI, PostgreSQL",
                                                    "languages": "Hungarian (native), English (fluent)",
                                                    "profile": "Passionate software engineer with interest in GenAI and Java development"
                                                },
                                                "extractedAt": "2024-01-15T10:35:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Extracted fields not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Not Found",
                                    value = """
                                            {
                                                "error": "Not Found",
                                                "message": "Extracted fields not found for request ID: 999",
                                                "timestamp": "2024-01-15T10:35:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                                            {
                                                "error": "Internal server error",
                                                "message": "An unexpected error occurred",
                                                "timestamp": "2024-01-15T10:35:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{requestId}/fields")
    public ResponseEntity<?> getExtractedFields(
            @Parameter(
                    description = "Unique identifier of the processing request",
                    required = true,
                    example = "1"
            )
            @PathVariable Long requestId) {
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
