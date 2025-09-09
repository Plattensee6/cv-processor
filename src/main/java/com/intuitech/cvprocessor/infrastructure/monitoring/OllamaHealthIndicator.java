package com.intuitech.cvprocessor.infrastructure.monitoring;

import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Health indicator for Ollama service integration with Spring Boot Actuator
 * 
 * Provides health status for Ollama service that can be monitored through
 * Spring Boot Actuator endpoints.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OllamaHealthIndicator implements HealthIndicator {

    private final OllamaHealthService ollamaHealthService;
    private final OllamaMetrics ollamaMetrics;

    @Override
    public Health health() {
        try {
            log.debug("Performing Ollama health check");
            
            // Get health information from service
            Map<String, Object> healthInfo = ollamaHealthService.getHealthInfo();
            boolean serviceAvailable = (Boolean) healthInfo.get("serviceAvailable");
            boolean modelAvailable = (Boolean) healthInfo.get("modelAvailable");
            String status = (String) healthInfo.get("status");
            
            // Update metrics
            ollamaMetrics.updateServiceStatus(serviceAvailable);
            ollamaMetrics.updateModelStatus(modelAvailable);
            
            // Record response time
            long responseTime = ollamaHealthService.getResponseTime();
            if (responseTime > 0) {
                ollamaMetrics.recordResponseTime(responseTime);
            }
            
            // Build health response
            Health.Builder healthBuilder = Health.up()
                    .withDetail("service", "Ollama")
                    .withDetail("host", healthInfo.get("host"))
                    .withDetail("port", healthInfo.get("port"))
                    .withDetail("model", healthInfo.get("model"))
                    .withDetail("serviceAvailable", serviceAvailable)
                    .withDetail("modelAvailable", modelAvailable)
                    .withDetail("responseTimeMs", responseTime)
                    .withDetail("timestamp", healthInfo.get("timestamp"));
            
            // Add metrics information
            Map<String, Object> metricsSummary = ollamaMetrics.getMetricsSummary();
            healthBuilder.withDetail("metrics", metricsSummary);
            
            // Determine overall health status
            if ("UP".equals(status) && serviceAvailable && modelAvailable) {
                return healthBuilder.build();
            } else {
                return healthBuilder.down()
                        .withDetail("reason", "Service or model not available")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Ollama health check failed: {}", e.getMessage());
            
            // Update metrics for failure
            ollamaMetrics.updateServiceStatus(false);
            ollamaMetrics.updateModelStatus(false);
            ollamaMetrics.recordFailure();
            
            return Health.down()
                    .withDetail("service", "Ollama")
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", java.time.LocalDateTime.now())
                    .build();
        }
    }
}
