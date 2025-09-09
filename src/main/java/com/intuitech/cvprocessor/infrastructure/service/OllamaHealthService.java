package com.intuitech.cvprocessor.infrastructure.service;

import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for monitoring Ollama health and status
 * 
 * Provides health checks, model availability, and performance metrics for Ollama service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaHealthService {

    private final RestTemplate restTemplate;
    private final OllamaConfig ollamaConfig;

    /**
     * Check if Ollama service is available
     * 
     * @return true if service is available, false otherwise
     */
    public boolean isServiceAvailable() {
        try {
            String healthUrl = ollamaConfig.getApiUrl() + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Ollama service health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if the configured model is available
     * 
     * @return true if model is available, false otherwise
     */
    public boolean isModelAvailable() {
        try {
            String modelUrl = ollamaConfig.getApiUrl() + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(modelUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                return responseBody != null && responseBody.contains(ollamaConfig.getModel());
            }
            return false;
        } catch (Exception e) {
            log.warn("Ollama model availability check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get detailed health information about Ollama service
     * 
     * @return health information map
     */
    public Map<String, Object> getHealthInfo() {
        boolean serviceAvailable = isServiceAvailable();
        boolean modelAvailable = isModelAvailable();
        
        String status = (serviceAvailable && modelAvailable) ? "UP" : "DOWN";
        
        return Map.of(
            "status", status,
            "service", "Ollama",
            "host", ollamaConfig.getHost(),
            "port", ollamaConfig.getPort(),
            "model", ollamaConfig.getModel(),
            "serviceAvailable", serviceAvailable,
            "modelAvailable", modelAvailable,
            "timestamp", LocalDateTime.now()
        );
    }

    /**
     * Test Ollama service response time
     * 
     * @return response time in milliseconds, -1 if failed
     */
    public long getResponseTime() {
        try {
            long startTime = System.currentTimeMillis();
            String healthUrl = ollamaConfig.getApiUrl() + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            long endTime = System.currentTimeMillis();
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return endTime - startTime;
            }
            return -1;
        } catch (Exception e) {
            log.warn("Ollama response time test failed: {}", e.getMessage());
            return -1;
        }
    }

    /**
     * Get Ollama service status asynchronously
     * 
     * @return CompletableFuture with health status
     */
    public CompletableFuture<Map<String, Object>> getHealthInfoAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getHealthInfo();
            } catch (Exception e) {
                log.error("Async health check failed: {}", e.getMessage());
                return Map.of(
                    "status", "DOWN",
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
                );
            }
        });
    }

    /**
     * Check if Ollama service is ready for processing
     * 
     * @return true if ready, false otherwise
     */
    public boolean isReady() {
        return isServiceAvailable() && isModelAvailable();
    }

    /**
     * Get service configuration information
     * 
     * @return configuration map
     */
    public Map<String, Object> getServiceInfo() {
        return Map.of(
            "host", ollamaConfig.getHost(),
            "port", ollamaConfig.getPort(),
            "model", ollamaConfig.getModel(),
            "timeout", ollamaConfig.getTimeout(),
            "apiUrl", ollamaConfig.getApiUrl(),
            "modelUrl", ollamaConfig.getModelUrl()
        );
    }
}
