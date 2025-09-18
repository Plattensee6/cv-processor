package com.intuitech.cvprocessor.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for Hugging Face integration
 * 
 * Sets up RestTemplate for Hugging Face API calls with proper configuration.
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
     * Create RestTemplate bean for HTTP calls
     * 
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        log.info("Initializing RestTemplate for Hugging Face API calls");
        
        return new RestTemplate();
    }

    /**
     * Get configured API key
     */
    public String getApiKey() {
        return apiKey;
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
    public Integer getTimeout() {
        return timeoutSeconds;
    }
}