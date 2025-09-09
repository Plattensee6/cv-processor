package com.intuitech.cvprocessor.infrastructure.config;

import com.intuitech.cvprocessor.application.service.ModelManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Initializes Ollama models on application startup
 * 
 * Automatically ensures the configured model is available when the application starts.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OllamaInitializer {

    private final ModelManagementService modelManagementService;
    private final OllamaConfig ollamaConfig;

    /**
     * Initialize Ollama model on application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(1000) // Run after other components are initialized
    public void initializeOllamaModel() {
        log.info("Starting Ollama model initialization...");
        log.info("Ollama configuration - Host: {}, Port: {}, Model: {}, Timeout: {}s", 
                ollamaConfig.getHost(), ollamaConfig.getPort(), 
                ollamaConfig.getModel(), ollamaConfig.getTimeout());
        
        try {
            Map<String, Object> result = modelManagementService.initialize();
            String status = (String) result.get("status");
            
            if ("available".equals(status) || "downloaded".equals(status)) {
                log.info("Ollama model initialization completed successfully: {}", result);
            } else {
                log.warn("Ollama model initialization completed with warnings: {}", result);
            }
            
        } catch (Exception e) {
            log.error("Ollama model initialization failed: {}", e.getMessage(), e);
            // Don't fail application startup, just log the error
        }
    }
}
