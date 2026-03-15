package com.intuitech.cvprocessor.infrastructure.controller;

import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for Ollama health check endpoints
 * 
 * Provides REST endpoints for monitoring Ollama service health and status.
 */
@RestController
@RequestMapping("/api/v1/health/ollama")
@RequiredArgsConstructor
@Slf4j
public class OllamaHealthController {

    private final OllamaHealthService ollamaHealthService;

    /**
     * Ollama service health check endpoint
     * 
     * @return Ollama service health status
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> ollamaHealth() {
        log.debug("Ollama health check requested");
        
        try {
            Map<String, Object> healthInfo = ollamaHealthService.getHealthInfo();
            String status = (String) healthInfo.get("status");
            
            if ("UP".equals(status)) {
                return ResponseEntity.ok(healthInfo);
            } else {
                return ResponseEntity.status(503).body(healthInfo);
            }
        } catch (Exception e) {
            log.error("Ollama health check failed: {}", e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "status", "DOWN",
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            );
            return ResponseEntity.status(503).body(errorResponse);
        }
    }

    /**
     * Ollama service detailed health information
     * 
     * @return detailed health information including response time
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> ollamaDetailedHealth() {
        log.debug("Ollama detailed health check requested");
        
        try {
            Map<String, Object> healthInfo = ollamaHealthService.getHealthInfo();
            long responseTime = ollamaHealthService.getResponseTime();
            
            healthInfo = Map.of(
                "status", healthInfo.get("status"),
                "service", healthInfo.get("service"),
                "host", healthInfo.getOrDefault("host", "unknown"),
                "port", healthInfo.getOrDefault("port", 0),
                "model", healthInfo.getOrDefault("model", "unknown"),
                "serviceAvailable", healthInfo.get("serviceAvailable"),
                "modelAvailable", healthInfo.get("modelAvailable"),
                "responseTimeMs", responseTime,
                "timestamp", healthInfo.get("timestamp")
            );
            
            String status = (String) healthInfo.get("status");
            if ("UP".equals(status)) {
                return ResponseEntity.ok(healthInfo);
            } else {
                return ResponseEntity.status(503).body(healthInfo);
            }
        } catch (Exception e) {
            log.error("Ollama detailed health check failed: {}", e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "status", "DOWN",
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            );
            return ResponseEntity.status(503).body(errorResponse);
        }
    }

    /**
     * Ollama service configuration information
     * 
     * @return service configuration details
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> ollamaConfig() {
        log.debug("Ollama configuration requested");
        
        try {
            Map<String, Object> configInfo = ollamaHealthService.getServiceInfo();
            return ResponseEntity.ok(configInfo);
        } catch (Exception e) {
            log.error("Failed to get Ollama configuration: {}", e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            );
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Asynchronous Ollama health check
     * 
     * @return CompletableFuture with health status
     */
    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> ollamaHealthAsync() {
        log.debug("Async Ollama health check requested");
        
        return ollamaHealthService.getHealthInfoAsync()
            .thenApply(healthInfo -> {
                String status = (String) healthInfo.get("status");
                if ("UP".equals(status)) {
                    return ResponseEntity.ok(healthInfo);
                } else {
                    return ResponseEntity.status(503).body(healthInfo);
                }
            })
            .exceptionally(throwable -> {
                log.error("Async Ollama health check failed: {}", throwable.getMessage());
                Map<String, Object> errorResponse = Map.of(
                    "status", "DOWN",
                    "error", throwable.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                );
                return ResponseEntity.status(503).body(errorResponse);
            });
    }

    /**
     * Quick Ollama readiness check
     * 
     * @return simple ready/not ready status
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ollamaReady() {
        log.debug("Ollama readiness check requested");
        
        try {
            boolean ready = ollamaHealthService.isReady();
            Map<String, Object> response = Map.of(
                "ready", ready,
                "timestamp", java.time.LocalDateTime.now()
            );
            
            if (ready) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(503).body(response);
            }
        } catch (Exception e) {
            log.error("Ollama readiness check failed: {}", e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "ready", false,
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now()
            );
            return ResponseEntity.status(503).body(errorResponse);
        }
    }
}
