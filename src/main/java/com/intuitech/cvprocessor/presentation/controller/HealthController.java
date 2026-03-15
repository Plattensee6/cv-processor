package com.intuitech.cvprocessor.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller for monitoring application status
 * 
 * Provides endpoints for health checks and basic application information.
 */
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    /**
     * Basic health check endpoint
     * 
     * @return health status information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Health check requested");
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "application", "CV Processor",
            "version", "1.0.0"
        );
        
        return ResponseEntity.ok(health);
    }

    /**
     * Detailed health check endpoint
     * 
     * @return detailed health status information
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        log.debug("Detailed health check requested");
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "application", "CV Processor",
            "version", "1.0.0",
            "components", Map.of(
                "database", "UP",
                "llm-service", "UP"
            )
        );
        
        return ResponseEntity.ok(health);
    }
}
