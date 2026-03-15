package com.intuitech.cvprocessor.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration properties for Ollama local model integration
 */
@Configuration
@ConfigurationProperties(prefix = "ollama")
@Data
public class OllamaConfig {

    /**
     * Ollama host and port configuration.
     */
    private String host = "localhost";
    private int port = 11434;

    /**
     * Model configuration.
     */
    private String model = "llama3.2:3b";
    private int timeout = 120;

    /**
     * Generation parameters, configurable from application.yaml:
     *
     * ollama:
     *   temperature: 0.1
     *   top-p: 0.9
     *   max-tokens: 2000
     */
    private double temperature = 0.1;
    private double topP = 0.9;
    private int maxTokens = 2000;
    
    /**
     * Get the full Ollama API URL
     */
    public String getApiUrl() {
        return String.format("http://%s:%d", host, port);
    }
    
    /**
     * Get the model endpoint URL
     */
    public String getModelUrl() {
        return String.format("%s/api/generate", getApiUrl());
    }
    
    /**
     * RestTemplate bean for HTTP communication with Ollama
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
