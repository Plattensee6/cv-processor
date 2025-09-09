package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing Ollama models
 * 
 * Handles model downloading, version management, and model operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModelManagementService {

    private final RestTemplate restTemplate;
    private final OllamaConfig ollamaConfig;
    private final OllamaHealthService ollamaHealthService;
    private final OllamaMetrics ollamaMetrics;

    /**
     * Download a model to Ollama
     * 
     * @param modelName the model name to download
     * @return download result information
     */
    public Map<String, Object> downloadModel(String modelName) {
        try {
            log.info("Starting model download: {}", modelName);
            long startTime = System.currentTimeMillis();
            
            String downloadUrl = ollamaConfig.getApiUrl() + "/api/pull";
            
            // Create request payload
            Map<String, Object> requestPayload = Map.of(
                "name", modelName,
                "stream", false
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
            
            // Make request
            restTemplate.exchange(
                downloadUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            long downloadTime = endTime - startTime;
            
            // Record metrics
            ollamaMetrics.recordModelLoadTime(downloadTime);
            
            log.info("Model download completed: {} in {}ms", modelName, downloadTime);
            
            return Map.of(
                "model", modelName,
                "status", "downloaded",
                "downloadTimeMs", downloadTime,
                "timestamp", LocalDateTime.now()
            );
            
        } catch (Exception e) {
            log.error("Model download failed for {}: {}", modelName, e.getMessage());
            ollamaMetrics.recordFailure();
            
            return Map.of(
                "model", modelName,
                "status", "failed",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
        }
    }

    /**
     * Download model asynchronously
     * 
     * @param modelName the model name to download
     * @return CompletableFuture with download result
     */
    public CompletableFuture<Map<String, Object>> downloadModelAsync(String modelName) {
        return CompletableFuture.supplyAsync(() -> downloadModel(modelName));
    }

    /**
     * List available models in Ollama
     * 
     * @return list of available models
     */
    public Map<String, Object> listModels() {
        try {
            log.debug("Listing available models");
            
            String listUrl = ollamaConfig.getApiUrl() + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(listUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ollamaMetrics.recordSuccess();
                
                return Map.of(
                    "status", "success",
                    "models", response.getBody(),
                    "timestamp", LocalDateTime.now()
                );
            } else {
                ollamaMetrics.recordFailure();
                
                return Map.of(
                    "status", "failed",
                    "error", "Failed to list models",
                    "timestamp", LocalDateTime.now()
                );
            }
            
        } catch (Exception e) {
            log.error("Failed to list models: {}", e.getMessage());
            ollamaMetrics.recordFailure();
            
            return Map.of(
                "status", "failed",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
        }
    }

    /**
     * Check if a specific model is available
     * 
     * @param modelName the model name to check
     * @return model availability information
     */
    public Map<String, Object> checkModelAvailability(String modelName) {
        try {
            log.debug("Checking model availability: {}", modelName);
            
            Map<String, Object> modelsInfo = listModels();
            boolean isAvailable = false;
            
            if ("success".equals(modelsInfo.get("status"))) {
                String modelsJson = (String) modelsInfo.get("models");
                isAvailable = modelsJson != null && modelsJson.contains(modelName);
            }
            
            return Map.of(
                "model", modelName,
                "available", isAvailable,
                "timestamp", LocalDateTime.now()
            );
            
        } catch (Exception e) {
            log.error("Failed to check model availability for {}: {}", modelName, e.getMessage());
            
            return Map.of(
                "model", modelName,
                "available", false,
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
        }
    }

    /**
     * Ensure the configured model is available
     * 
     * @return model availability status
     */
    public Map<String, Object> ensureModelAvailable() {
        String configuredModel = ollamaConfig.getModel();
        log.info("Ensuring model availability: {}", configuredModel);
        
        Map<String, Object> availability = checkModelAvailability(configuredModel);
        boolean isAvailable = (Boolean) availability.get("available");
        
        if (!isAvailable) {
            log.info("Model {} not available, attempting download", configuredModel);
            return downloadModel(configuredModel);
        } else {
            log.info("Model {} is already available", configuredModel);
            return Map.of(
                "model", configuredModel,
                "status", "available",
                "timestamp", LocalDateTime.now()
            );
        }
    }

    /**
     * Get model information
     * 
     * @param modelName the model name
     * @return model information
     */
    public Map<String, Object> getModelInfo(String modelName) {
        try {
            log.debug("Getting model info for: {}", modelName);
            
            String infoUrl = ollamaConfig.getApiUrl() + "/api/show";
            
            Map<String, Object> requestPayload = Map.of("name", modelName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                infoUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ollamaMetrics.recordSuccess();
                
                return Map.of(
                    "model", modelName,
                    "info", response.getBody(),
                    "timestamp", LocalDateTime.now()
                );
            } else {
                ollamaMetrics.recordFailure();
                
                return Map.of(
                    "model", modelName,
                    "status", "failed",
                    "error", "Failed to get model info",
                    "timestamp", LocalDateTime.now()
                );
            }
            
        } catch (Exception e) {
            log.error("Failed to get model info for {}: {}", modelName, e.getMessage());
            ollamaMetrics.recordFailure();
            
            return Map.of(
                "model", modelName,
                "status", "failed",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
        }
    }

    /**
     * Initialize model management (download configured model if needed)
     * 
     * @return initialization result
     */
    public Map<String, Object> initialize() {
        log.info("Initializing model management");
        
        try {
            // Check if Ollama service is available
            if (!ollamaHealthService.isServiceAvailable()) {
                return Map.of(
                    "status", "failed",
                    "error", "Ollama service not available",
                    "timestamp", LocalDateTime.now()
                );
            }
            
            // Ensure configured model is available
            return ensureModelAvailable();
            
        } catch (Exception e) {
            log.error("Model management initialization failed: {}", e.getMessage());
            
            return Map.of(
                "status", "failed",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
        }
    }
}
