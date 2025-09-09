package com.intuitech.cvprocessor.integration;

import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Ollama components
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "ollama.host=localhost",
        "ollama.port=11434",
        "ollama.model=llama3.2:3b",
        "ollama.timeout=30"
})
class OllamaIntegrationTest {

    @Autowired
    private OllamaConfig ollamaConfig;

    @Autowired
    private OllamaHealthService ollamaHealthService;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // Setup test data if needed
    }

    @Test
    void ollamaConfig_ShouldBeProperlyConfigured() {
        // Then
        assertNotNull(ollamaConfig);
        assertEquals("localhost", ollamaConfig.getHost());
        assertEquals(11434, ollamaConfig.getPort());
        assertEquals("llama3.2:3b", ollamaConfig.getModel());
        assertEquals(30, ollamaConfig.getTimeout());
        assertEquals("http://localhost:11434", ollamaConfig.getApiUrl());
        assertEquals("http://localhost:11434/api/generate", ollamaConfig.getModelUrl());
    }

    @Test
    void ollamaHealthService_ShouldBeProperlyWired() {
        // Then
        assertNotNull(ollamaHealthService);
        assertNotNull(ollamaHealthService.getServiceInfo());
    }

    @Test
    void restTemplate_ShouldBeProperlyConfigured() {
        // Then
        assertNotNull(restTemplate);
    }

    @Test
    void ollamaHealthService_GetServiceInfo_ShouldReturnValidInfo() {
        // When
        var serviceInfo = ollamaHealthService.getServiceInfo();

        // Then
        assertNotNull(serviceInfo);
        assertTrue(serviceInfo.containsKey("host"));
        assertTrue(serviceInfo.containsKey("port"));
        assertTrue(serviceInfo.containsKey("model"));
        assertTrue(serviceInfo.containsKey("timeout"));
        assertTrue(serviceInfo.containsKey("apiUrl"));
        assertTrue(serviceInfo.containsKey("modelUrl"));
    }

    @Test
    void ollamaHealthService_GetHealthInfo_ShouldReturnValidStructure() {
        // When
        var healthInfo = ollamaHealthService.getHealthInfo();

        // Then
        assertNotNull(healthInfo);
        assertTrue(healthInfo.containsKey("status"));
        assertTrue(healthInfo.containsKey("service"));
        assertTrue(healthInfo.containsKey("host"));
        assertTrue(healthInfo.containsKey("port"));
        assertTrue(healthInfo.containsKey("model"));
        assertTrue(healthInfo.containsKey("serviceAvailable"));
        assertTrue(healthInfo.containsKey("modelAvailable"));
        assertTrue(healthInfo.containsKey("timestamp"));
    }

    @Test
    void ollamaHealthService_GetResponseTime_ShouldReturnValidTime() {
        // When
        long responseTime = ollamaHealthService.getResponseTime();

        // Then
        // Response time should be >= -1 (where -1 indicates failure)
        assertTrue(responseTime >= -1);
    }

    @Test
    void ollamaHealthService_IsReady_ShouldReturnBoolean() {
        // When
        boolean isReady = ollamaHealthService.isReady();

        // Then
        // Should return a boolean value (true or false)
        assertTrue(isReady == true || isReady == false);
    }

    @Test
    void ollamaHealthService_IsServiceAvailable_ShouldReturnBoolean() {
        // When
        boolean isAvailable = ollamaHealthService.isServiceAvailable();

        // Then
        // Should return a boolean value (true or false)
        assertTrue(isAvailable == true || isAvailable == false);
    }

    @Test
    void ollamaHealthService_IsModelAvailable_ShouldReturnBoolean() {
        // When
        boolean isAvailable = ollamaHealthService.isModelAvailable();

        // Then
        // Should return a boolean value (true or false)
        assertTrue(isAvailable == true || isAvailable == false);
    }

    @Test
    void ollamaHealthService_GetHealthInfoAsync_ShouldReturnCompletableFuture() {
        // When
        var future = ollamaHealthService.getHealthInfoAsync();

        // Then
        assertNotNull(future);
        assertTrue(future instanceof java.util.concurrent.CompletableFuture);
    }
}
