package com.intuitech.cvprocessor.infrastructure.config;

import ai.huggingface.HuggingFaceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Hugging Face integration
 * 
 * Sets up Hugging Face client with proper configuration for open-source models.
 */
@Configuration
@Slf4j
public class HuggingFaceConfig {

    @Value("${huggingface.api-key:}")
    private String apiKey;

    @Value("${huggingface.model:openai/gpt-4.1}")
    private String model;

    @Value("${huggingface.max-tokens:2000}")
    private Integer maxTokens;

    @Value("${huggingface.temperature:0.1}")
    private Double temperature;

    @Value("${huggingface.timeout:60}")
    private Integer timeoutSeconds;

    /**
     * Create Hugging Face API client bean
     * 
     * @return configured Hugging Face API client
     */
    @Bean
    public HuggingFaceApi huggingFaceApi() {
        log.info("Initializing Hugging Face API with model: {}", model);
        
        HuggingFaceApi.Builder builder = HuggingFaceApi.builder();
        
        if (apiKey != null && !apiKey.isEmpty()) {
            builder.apiKey(apiKey);
        }
        
        return builder.build();
    }

    /**
     * Get configured model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Get configured max tokens
     */
    public Integer getMaxTokens() {
        return maxTokens;
    }

    /**
     * Get configured temperature
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * Get configured timeout in seconds
     */
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }
}