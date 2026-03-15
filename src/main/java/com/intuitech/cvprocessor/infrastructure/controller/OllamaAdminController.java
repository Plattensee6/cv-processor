package com.intuitech.cvprocessor.infrastructure.controller;

import com.intuitech.cvprocessor.application.service.ModelManagementService;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin controller for Ollama model management
 * 
 * Provides endpoints for manual model management operations.
 */
@RestController
@RequestMapping("/api/v1/admin/ollama")
@RequiredArgsConstructor
@Slf4j
public class OllamaAdminController {

    private final ModelManagementService modelManagementService;
    private final OllamaMetrics ollamaMetrics;

    /**
     * Download a specific model
     */
    @PostMapping("/models/{modelName}/download")
    public ResponseEntity<Map<String, Object>> downloadModel(@PathVariable String modelName) {
        log.info("Manual model download requested: {}", modelName);
        
        try {
            Map<String, Object> result = modelManagementService.downloadModel(modelName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Manual model download failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        }
    }

    /**
     * List available models
     */
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> listModels() {
        log.info("Manual model list requested");
        
        try {
            Map<String, Object> result = modelManagementService.listModels();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Manual model list failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        }
    }

    /**
     * Check model availability
     */
    @GetMapping("/models/{modelName}/status")
    public ResponseEntity<Map<String, Object>> checkModelStatus(@PathVariable String modelName) {
        log.info("Manual model status check requested: {}", modelName);
        
        try {
            Map<String, Object> result = modelManagementService.checkModelAvailability(modelName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Manual model status check failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        }
    }

    /**
     * Initialize model management
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initialize() {
        log.info("Manual model initialization requested");
        
        try {
            Map<String, Object> result = modelManagementService.initialize();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Manual model initialization failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        }
    }

    /**
     * Get metrics summary
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        log.info("Manual metrics request");
        
        try {
            Map<String, Object> metrics = ollamaMetrics.getMetricsSummary();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Manual metrics request failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        }
    }

    /**
     * Reset metrics
     */
    @PostMapping("/metrics/reset")
    public ResponseEntity<Map<String, Object>> resetMetrics() {
        log.info("Manual metrics reset requested");
        
        try {
            ollamaMetrics.reset();
            return ResponseEntity.ok(Map.of(
                "message", "Metrics reset successfully",
                "timestamp", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Manual metrics reset failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        }
    }
}
