package com.intuitech.cvprocessor.infrastructure.service;

import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OllamaHealthService
 */
@ExtendWith(MockitoExtension.class)
class OllamaHealthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OllamaConfig ollamaConfig;

    @InjectMocks
    private OllamaHealthService ollamaHealthService;

    @BeforeEach
    void setUp() {
        lenient().when(ollamaConfig.getHost()).thenReturn("localhost");
        lenient().when(ollamaConfig.getPort()).thenReturn(11434);
        lenient().when(ollamaConfig.getModel()).thenReturn("llama3.2:3b");
        lenient().when(ollamaConfig.getApiUrl()).thenReturn("http://localhost:11434");
    }

    @Test
    void isServiceAvailable_WhenServiceIsUp_ShouldReturnTrue() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("{\"models\":[]}", HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        boolean result = ollamaHealthService.isServiceAvailable();

        // Then
        assertTrue(result);
        verify(restTemplate).getForEntity("http://localhost:11434/api/tags", String.class);
    }

    @Test
    void isServiceAvailable_WhenServiceIsDown_ShouldReturnFalse() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // When
        boolean result = ollamaHealthService.isServiceAvailable();

        // Then
        assertFalse(result);
    }

    @Test
    void isModelAvailable_WhenModelExists_ShouldReturnTrue() {
        // Given
        String responseBody = "{\"models\":[{\"name\":\"llama3.2:3b\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        boolean result = ollamaHealthService.isModelAvailable();

        // Then
        assertTrue(result);
    }

    @Test
    void isModelAvailable_WhenModelDoesNotExist_ShouldReturnFalse() {
        // Given
        String responseBody = "{\"models\":[{\"name\":\"other-model\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        boolean result = ollamaHealthService.isModelAvailable();

        // Then
        assertFalse(result);
    }

    @Test
    void getHealthInfo_WhenServiceIsUp_ShouldReturnUpStatus() {
        // Given
        String responseBody = "{\"models\":[{\"name\":\"llama3.2:3b\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = ollamaHealthService.getHealthInfo();

        // Then
        assertEquals("UP", result.get("status"));
        assertEquals("Ollama", result.get("service"));
        assertEquals("localhost", result.get("host"));
        assertEquals(11434, result.get("port"));
        assertEquals("llama3.2:3b", result.get("model"));
        assertTrue((Boolean) result.get("serviceAvailable"));
        assertTrue((Boolean) result.get("modelAvailable"));
    }

    @Test
    void getHealthInfo_WhenServiceIsDown_ShouldReturnDownStatus() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // When
        Map<String, Object> result = ollamaHealthService.getHealthInfo();

        // Then
        assertEquals("DOWN", result.get("status"));
        assertFalse((Boolean) result.get("serviceAvailable"));
        assertFalse((Boolean) result.get("modelAvailable"));
    }

    @Test
    void getResponseTime_WhenServiceIsUp_ShouldReturnPositiveTime() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("{\"models\":[]}", HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        long result = ollamaHealthService.getResponseTime();

        // Then
        assertTrue(result >= 0);
    }

    @Test
    void getResponseTime_WhenServiceIsDown_ShouldReturnNegativeOne() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // When
        long result = ollamaHealthService.getResponseTime();

        // Then
        assertEquals(-1, result);
    }

    @Test
    void isReady_WhenServiceAndModelAreAvailable_ShouldReturnTrue() {
        // Given
        String responseBody = "{\"models\":[{\"name\":\"llama3.2:3b\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        boolean result = ollamaHealthService.isReady();

        // Then
        assertTrue(result);
    }

    @Test
    void isReady_WhenServiceIsDown_ShouldReturnFalse() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // When
        boolean result = ollamaHealthService.isReady();

        // Then
        assertFalse(result);
    }
}
