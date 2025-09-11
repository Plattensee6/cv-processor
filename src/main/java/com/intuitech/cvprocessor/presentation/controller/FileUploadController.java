package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.application.dto.FileUploadResponseDTO;
import com.intuitech.cvprocessor.application.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.Map;

/**
 * REST controller for file upload operations
 * 
 * Handles CV file uploads and provides endpoints for retrieving upload status.
 */
@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Upload", description = "CV file upload and status management endpoints")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload a CV file for processing
     * 
     * @param file the CV file to upload
     * @return response with upload information
     */
    @Operation(
            summary = "Upload CV file",
            description = "Upload a CV file (PDF, DOC, DOCX) for processing. The file will be parsed and stored for further processing."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FileUploadResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                                "requestId": 1,
                                                "fileName": "john_doe_cv.pdf",
                                                "contentType": "application/pdf",
                                                "fileSize": 245760,
                                                "status": "UPLOADED",
                                                "message": "File uploaded and parsed successfully",
                                                "uploadedAt": "2024-01-15T10:30:00",
                                                "parsedText": "John Doe\\nSoftware Engineer..."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid file or validation error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Error Response",
                                    value = """
                                            {
                                                "error": "File upload failed",
                                                "message": "File size exceeds maximum allowed size",
                                                "timestamp": "2024-01-15T10:30:00",
                                                "path": "/api/cv/upload"
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
                                                "timestamp": "2024-01-15T10:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @Parameter(
                    description = "CV file to upload (PDF, DOC, DOCX). Maximum size: 10MB",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestParam("file") @Valid MultipartFile file) {
        log.info("Received file upload request: {}", file.getOriginalFilename());

        try {
            FileUploadResponseDTO response = fileUploadService.uploadFile(file);
            return ResponseEntity.ok(response);

        } catch (FileUploadService.FileUploadException e) {
            log.error("File upload failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "File upload failed",
                            "message", e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now(),
                            "path", "/api/cv/upload"
                    ));
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Internal server error",
                            "message", "An unexpected error occurred",
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        }
    }

    /**
     * Get processing request status
     * 
     * @param requestId the request ID
     * @return processing request information
     */
    @Operation(
            summary = "Get processing status",
            description = "Retrieve the current status and details of a CV processing request by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Processing status retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FileUploadResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                                "requestId": 1,
                                                "fileName": "john_doe_cv.pdf",
                                                "contentType": "application/pdf",
                                                "fileSize": 245760,
                                                "status": "COMPLETED",
                                                "message": "File uploaded and parsed successfully",
                                                "uploadedAt": "2024-01-15T10:30:00",
                                                "parsedText": "John Doe\\nSoftware Engineer..."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Processing request not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Not Found",
                                    value = """
                                            {
                                                "error": "Not Found",
                                                "message": "Processing request not found: 999",
                                                "timestamp": "2024-01-15T10:30:00",
                                                "path": "/api/cv/status/999"
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
                                                "timestamp": "2024-01-15T10:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/status/{requestId}")
    public ResponseEntity<?> getProcessingStatus(
            @Parameter(
                    description = "Unique identifier of the processing request",
                    required = true,
                    example = "1"
            )
            @PathVariable @Valid Long requestId) {
        log.debug("Retrieving processing status for request: {}", requestId);

        try {
            FileUploadResponseDTO response = fileUploadService.getProcessingRequest(requestId);
            return ResponseEntity.ok(response);

        } catch (FileUploadService.FileUploadException e) {
            log.error("Failed to retrieve processing status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Not Found",
                            "message", e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now(),
                            "path", "/api/cv/status/" + requestId
                    ));

        } catch (Exception e) {
            log.error("Unexpected error retrieving processing status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Internal server error",
                            "message", "An unexpected error occurred",
                            "timestamp", java.time.LocalDateTime.now()
                    ));
        }
    }

    /**
     * Health check for file upload service
     * 
     * @return service health status
     */
    @Operation(
            summary = "Health check",
            description = "Check the health status of the file upload service and available endpoints."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Health Status",
                            value = """
                                    {
                                        "status": "UP",
                                        "service": "File Upload Service",
                                        "timestamp": "2024-01-15T10:30:00",
                                        "endpoints": {
                                            "upload": "POST /api/cv/upload",
                                            "status": "GET /api/cv/status/{requestId}"
                                        }
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("File upload service health check requested");

        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "File Upload Service",
                "timestamp", java.time.LocalDateTime.now(),
                "endpoints", Map.of(
                        "upload", "POST /api/cv/upload",
                        "status", "GET /api/cv/status/{requestId}"
                )
        );

        return ResponseEntity.ok(health);
    }
}
