package com.intuitech.cvprocessor.infrastructure.controller;

import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OllamaHealthController
 */
@ExtendWith(MockitoExtension.class)
class OllamaHealthControllerTest {

    @Mock
    private OllamaHealthService ollamaHealthService;

    @InjectMocks
    private OllamaHealthController ollamaHealthController;

    private Map<String, Object> mockHealthInfo;

    @BeforeEach
    void setUp() {
        mockHealthInfo = Map.of(
                "status", "UP",
                "service", "Ollama",
                "host", "localhost",
                "port", 11434,
                "model", "llama3.2:3b",
                "serviceAvailable", true,
                "modelAvailable", true,
                "timestamp", java.time.LocalDateTime.now()
        );
    }

    @Test
    void ollamaHealth_WithServiceUp_ShouldReturnOk() {
        // Given
        when(ollamaHealthService.getHealthInfo()).thenReturn(mockHealthInfo);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaHealth();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("UP", responseBody.get("status"));
        assertEquals("Ollama", responseBody.get("service"));

        verify(ollamaHealthService).getHealthInfo();
    }

    @Test
    void ollamaHealth_WithServiceDown_ShouldReturnServiceUnavailable() {
        // Given
        Map<String, Object> downHealthInfo = Map.of(
                "status", "DOWN",
                "service", "Ollama",
                "serviceAvailable", false,
                "modelAvailable", false,
                "timestamp", java.time.LocalDateTime.now()
        );
        when(ollamaHealthService.getHealthInfo()).thenReturn(downHealthInfo);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaHealth();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("DOWN", responseBody.get("status"));

        verify(ollamaHealthService).getHealthInfo();
    }

    @Test
    void ollamaHealth_WithException_ShouldReturnServiceUnavailable() {
        // Given
        when(ollamaHealthService.getHealthInfo()).thenThrow(new RuntimeException("Health check failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaHealth();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("DOWN", responseBody.get("status"));
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaHealthService).getHealthInfo();
    }

    @Test
    void ollamaDetailedHealth_WithServiceUp_ShouldReturnOk() {
        // Given
        when(ollamaHealthService.getHealthInfo()).thenReturn(mockHealthInfo);
        when(ollamaHealthService.getResponseTime()).thenReturn(150L);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaDetailedHealth();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("UP", responseBody.get("status"));
        assertEquals("Ollama", responseBody.get("service"));
        assertEquals(150L, responseBody.get("responseTimeMs"));

        verify(ollamaHealthService).getHealthInfo();
        verify(ollamaHealthService).getResponseTime();
    }

    @Test
    void ollamaDetailedHealth_WithServiceDown_ShouldReturnServiceUnavailable() {
        // Given
        Map<String, Object> downHealthInfo = Map.of(
                "status", "DOWN",
                "service", "Ollama",
                "serviceAvailable", false,
                "modelAvailable", false,
                "timestamp", java.time.LocalDateTime.now()
        );
        when(ollamaHealthService.getHealthInfo()).thenReturn(downHealthInfo);
        when(ollamaHealthService.getResponseTime()).thenReturn(-1L);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaDetailedHealth();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("DOWN", responseBody.get("status"));
        assertEquals(-1L, responseBody.get("responseTimeMs"));

        verify(ollamaHealthService).getHealthInfo();
        verify(ollamaHealthService).getResponseTime();
    }

    @Test
    void ollamaDetailedHealth_WithException_ShouldReturnServiceUnavailable() {
        // Given
        when(ollamaHealthService.getHealthInfo()).thenThrow(new RuntimeException("Detailed health check failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaDetailedHealth();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("DOWN", responseBody.get("status"));
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaHealthService).getHealthInfo();
    }

    @Test
    void ollamaConfig_WithValidConfig_ShouldReturnOk() {
        // Given
        Map<String, Object> configInfo = Map.of(
                "host", "localhost",
                "port", 11434,
                "model", "llama3.2:3b",
                "timeout", 120,
                "apiUrl", "http://localhost:11434",
                "modelUrl", "http://localhost:11434/api/generate"
        );
        when(ollamaHealthService.getServiceInfo()).thenReturn(configInfo);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaConfig();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("localhost", responseBody.get("host"));
        assertEquals(11434, responseBody.get("port"));
        assertEquals("llama3.2:3b", responseBody.get("model"));

        verify(ollamaHealthService).getServiceInfo();
    }

    @Test
    void ollamaConfig_WithException_ShouldReturnInternalServerError() {
        // Given
        when(ollamaHealthService.getServiceInfo()).thenThrow(new RuntimeException("Config failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaConfig();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaHealthService).getServiceInfo();
    }

    @Test
    void ollamaHealthAsync_WithServiceUp_ShouldReturnOk() {
        // Given
        CompletableFuture<Map<String, Object>> future = CompletableFuture.completedFuture(mockHealthInfo);
        when(ollamaHealthService.getHealthInfoAsync()).thenReturn(future);

        // When
        CompletableFuture<ResponseEntity<Map<String, Object>>> responseFuture = 
                ollamaHealthController.ollamaHealthAsync();

        // Then
        assertNotNull(responseFuture);
        assertTrue(responseFuture.isDone());
        
        ResponseEntity<Map<String, Object>> response = responseFuture.join();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("UP", responseBody.get("status"));

        verify(ollamaHealthService).getHealthInfoAsync();
    }

    @Test
    void ollamaHealthAsync_WithServiceDown_ShouldReturnServiceUnavailable() {
        // Given
        Map<String, Object> downHealthInfo = Map.of(
                "status", "DOWN",
                "service", "Ollama",
                "serviceAvailable", false,
                "modelAvailable", false,
                "timestamp", java.time.LocalDateTime.now()
        );
        CompletableFuture<Map<String, Object>> future = CompletableFuture.completedFuture(downHealthInfo);
        when(ollamaHealthService.getHealthInfoAsync()).thenReturn(future);

        // When
        CompletableFuture<ResponseEntity<Map<String, Object>>> responseFuture = 
                ollamaHealthController.ollamaHealthAsync();

        // Then
        assertNotNull(responseFuture);
        assertTrue(responseFuture.isDone());
        
        ResponseEntity<Map<String, Object>> response = responseFuture.join();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("DOWN", responseBody.get("status"));

        verify(ollamaHealthService).getHealthInfoAsync();
    }

    @Test
    void ollamaHealthAsync_WithException_ShouldReturnServiceUnavailable() {
        // Given
        CompletableFuture<Map<String, Object>> future = CompletableFuture.failedFuture(
                new RuntimeException("Async health check failed"));
        when(ollamaHealthService.getHealthInfoAsync()).thenReturn(future);

        // When
        CompletableFuture<ResponseEntity<Map<String, Object>>> responseFuture = 
                ollamaHealthController.ollamaHealthAsync();

        // Then
        assertNotNull(responseFuture);
        assertTrue(responseFuture.isDone());
        
        ResponseEntity<Map<String, Object>> response = responseFuture.join();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("DOWN", responseBody.get("status"));
        assertTrue(responseBody.containsKey("error"));

        verify(ollamaHealthService).getHealthInfoAsync();
    }

    @Test
    void ollamaReady_WithServiceReady_ShouldReturnOk() {
        // Given
        when(ollamaHealthService.isReady()).thenReturn(true);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaReady();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("ready"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaHealthService).isReady();
    }

    @Test
    void ollamaReady_WithServiceNotReady_ShouldReturnServiceUnavailable() {
        // Given
        when(ollamaHealthService.isReady()).thenReturn(false);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaReady();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("ready"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaHealthService).isReady();
    }

    @Test
    void ollamaReady_WithException_ShouldReturnServiceUnavailable() {
        // Given
        when(ollamaHealthService.isReady()).thenThrow(new RuntimeException("Ready check failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaHealthController.ollamaReady();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("ready"));
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaHealthService).isReady();
    }
}
