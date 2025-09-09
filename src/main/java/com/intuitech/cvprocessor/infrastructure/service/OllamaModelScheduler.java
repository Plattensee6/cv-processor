package com.intuitech.cvprocessor.infrastructure.service;

import com.intuitech.cvprocessor.application.service.ModelManagementService;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Scheduled service for monitoring and maintaining Ollama models
 * 
 * Periodically checks model availability and ensures models are ready.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaModelScheduler {

    private final ModelManagementService modelManagementService;
    private final OllamaHealthService ollamaHealthService;
    private final OllamaMetrics ollamaMetrics;

    /**
     * Check model availability every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void checkModelAvailability() {
        log.debug("Scheduled model availability check");
        
        try {
            boolean serviceAvailable = ollamaHealthService.isServiceAvailable();
            boolean modelAvailable = ollamaHealthService.isModelAvailable();
            
            // Update metrics
            ollamaMetrics.updateServiceStatus(serviceAvailable);
            ollamaMetrics.updateModelStatus(modelAvailable);
            
            if (!serviceAvailable) {
                log.warn("Ollama service is not available");
                return;
            }
            
            if (!modelAvailable) {
                log.warn("Configured model is not available, attempting to ensure availability");
                Map<String, Object> result = modelManagementService.ensureModelAvailable();
                log.info("Model availability check result: {}", result);
            }
            
        } catch (Exception e) {
            log.error("Scheduled model availability check failed: {}", e.getMessage());
            ollamaMetrics.recordFailure();
        }
    }

    /**
     * Health check every minute
     */
    @Scheduled(fixedRate = 60000) // 1 minute
    public void performHealthCheck() {
        log.debug("Scheduled health check");
        
        try {
            long responseTime = ollamaHealthService.getResponseTime();
            if (responseTime > 0) {
                ollamaMetrics.recordResponseTime(responseTime);
            }
        } catch (Exception e) {
            log.debug("Scheduled health check failed: {}", e.getMessage());
        }
    }
}
