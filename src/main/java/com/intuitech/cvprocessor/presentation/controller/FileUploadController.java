package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.application.dto.FileUploadResponseDTO;
import com.intuitech.cvprocessor.application.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload a CV file for processing
     * 
     * @param file the CV file to upload
     * @return response with upload information
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") @Valid MultipartFile file) {
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
    @GetMapping("/status/{requestId}")
    public ResponseEntity<?> getProcessingStatus(@PathVariable @Valid Long requestId) {
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
