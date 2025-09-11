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
    
    private String host = "localhost";
    private int port = 11434;
    private String model = "llama3.2:3b";
    private int timeout = 120;
    
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
