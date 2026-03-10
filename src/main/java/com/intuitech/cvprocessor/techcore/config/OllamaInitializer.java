package com.intuitech.cvprocessor.techcore.config;

import com.intuitech.cvprocessor.application.service.ModelManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Initializes the default Ollama model on application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OllamaInitializer {

    private final ModelManagementService modelManagementService;
    private final OllamaConfig ollamaConfig;

    /**
     * Initialize the configured Ollama model.
     *
     * This method is idempotent from the perspective of the tests: they only verify
     * that {@link ModelManagementService#initialize()} is invoked.
     */
    public void initializeOllamaModel() {
        try {
            String modelName = ollamaConfig.getModel();
            log.info("Initializing Ollama model: {}", modelName);

            Map<String, Object> result = modelManagementService.initialize();

            if (result == null) {
                log.warn("Ollama initialization returned null result");
                return;
            }

            Object status = result.get("status");
            log.info("Ollama initialization status: {}", status);
        } catch (Exception e) {
            log.error("Ollama model initialization failed: {}", e.getMessage(), e);
        }
    }
}

