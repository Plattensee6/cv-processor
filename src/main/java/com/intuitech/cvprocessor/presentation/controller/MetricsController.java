package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.infrastructure.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for application metrics
 * 
 * Provides endpoints for accessing application metrics and statistics.
 */
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Slf4j
public class MetricsController {

    private final MetricsService metricsService;

    /**
     * Get application metrics
     * 
     * @return application metrics
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMetrics() {
        log.debug("Retrieving application metrics");

        Map<String, Object> metrics = Map.of(
                "processingSuccessRate", metricsService.getProcessingSuccessRate(),
                "validationSuccessRate", metricsService.getValidationSuccessRate(),
                "timestamp", java.time.LocalDateTime.now()
        );

        return ResponseEntity.ok(metrics);
    }

    /**
     * Get detailed metrics
     * 
     * @return detailed application metrics
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedMetrics() {
        log.debug("Retrieving detailed application metrics");

        Map<String, Object> metrics = Map.of(
                "processing", Map.of(
                        "successRate", metricsService.getProcessingSuccessRate(),
                        "description", "CV processing success rate"
                ),
                "validation", Map.of(
                        "successRate", metricsService.getValidationSuccessRate(),
                        "description", "Validation success rate"
                ),
                "timestamp", java.time.LocalDateTime.now(),
                "note", "Detailed metrics available via /actuator/metrics endpoint"
        );

        return ResponseEntity.ok(metrics);
    }

    /**
     * Health check for metrics service
     * 
     * @return service health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Metrics service health check requested");

        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "Metrics Service",
                "timestamp", java.time.LocalDateTime.now(),
                "endpoints", Map.of(
                        "metrics", "GET /api/metrics",
                        "detailed", "GET /api/metrics/detailed"
                )
        );

        return ResponseEntity.ok(health);
    }
}
