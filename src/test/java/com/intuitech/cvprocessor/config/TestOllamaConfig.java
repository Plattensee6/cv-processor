package com.intuitech.cvprocessor.config;

import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test configuration for Ollama services
 * 
 * Provides mocked Ollama services to avoid external dependencies during testing.
 */
@TestConfiguration
@Profile("test")
public class TestOllamaConfig {

    /**
     * Mock RestTemplate for Ollama services
     */
    @Bean
    @Primary
    public RestTemplate mockRestTemplate() {
        return mock(RestTemplate.class);
    }

    /**
     * Mock OllamaHealthService to avoid external HTTP calls
     */
    @Bean
    @Primary
    public OllamaHealthService mockOllamaHealthService() {
        OllamaHealthService mockService = mock(OllamaHealthService.class);
        
        // Mock service info
        when(mockService.getServiceInfo()).thenReturn(Map.of(
            "host", "localhost",
            "port", 11434,
            "model", "llama3.2:3b",
            "timeout", 30,
            "apiUrl", "http://localhost:11434",
            "modelUrl", "http://localhost:11434/api/generate"
        ));
        
        // Mock health info
        when(mockService.getHealthInfo()).thenReturn(Map.of(
            "status", "DOWN",
            "service", "Ollama",
            "host", "localhost",
            "port", 11434,
            "model", "llama3.2:3b",
            "serviceAvailable", false,
            "modelAvailable", false,
            "timestamp", System.currentTimeMillis()
        ));
        
        // Mock async health info
        when(mockService.getHealthInfoAsync()).thenReturn(
            CompletableFuture.completedFuture(Map.of(
                "status", "DOWN",
                "service", "Ollama",
                "host", "localhost",
                "port", 11434,
                "model", "llama3.2:3b",
                "serviceAvailable", false,
                "modelAvailable", false,
                "timestamp", System.currentTimeMillis()
            ))
        );
        
        // Mock other methods
        when(mockService.getResponseTime()).thenReturn(-1L);
        when(mockService.isReady()).thenReturn(false);
        when(mockService.isServiceAvailable()).thenReturn(false);
        when(mockService.isModelAvailable()).thenReturn(false);
        
        return mockService;
    }
}
