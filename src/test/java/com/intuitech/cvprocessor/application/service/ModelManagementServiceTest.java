package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
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
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ModelManagementService
 */
@ExtendWith(MockitoExtension.class)
class ModelManagementServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OllamaConfig ollamaConfig;

    @Mock
    private OllamaHealthService ollamaHealthService;

    @Mock
    private OllamaMetrics ollamaMetrics;

    @InjectMocks
    private ModelManagementService modelManagementService;

    private final String testModelName = "llama3.2:3b";
    private final String testApiUrl = "http://localhost:11434";

    @BeforeEach
    void setUp() {
        lenient().when(ollamaConfig.getApiUrl()).thenReturn(testApiUrl);
        lenient().when(ollamaConfig.getModel()).thenReturn(testModelName);
    }

    @Test
    void downloadModel_WithSuccessfulDownload_ShouldReturnSuccess() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = modelManagementService.downloadModel(testModelName);

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertEquals("downloaded", result.get("status"));
        assertTrue(result.containsKey("downloadTimeMs"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaMetrics).recordModelLoadTime(anyLong());
        verify(restTemplate).exchange(
                eq(testApiUrl + "/api/pull"),
                any(),
                any(),
                eq(String.class)
        );
    }

    @Test
    void downloadModel_WithFailedDownload_ShouldReturnFailure() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Download failed"));

        // When
        Map<String, Object> result = modelManagementService.downloadModel(testModelName);

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertEquals("failed", result.get("status"));
        assertTrue(result.containsKey("error"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaMetrics).recordFailure();
    }

    @Test
    void downloadModelAsync_ShouldReturnCompletableFuture() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When
        CompletableFuture<Map<String, Object>> future = modelManagementService.downloadModelAsync(testModelName);

        // Then
        assertNotNull(future);
        
        Map<String, Object> result = future.join();
        assertEquals("downloaded", result.get("status"));
    }

    @Test
    void listModels_WithSuccessfulResponse_ShouldReturnModels() {
        // Given
        String modelsResponse = "{\"models\":[{\"name\":\"llama3.2:3b\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(modelsResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = modelManagementService.listModels();

        // Then
        assertNotNull(result);
        assertEquals("success", result.get("status"));
        assertEquals(modelsResponse, result.get("models"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaMetrics).recordSuccess();
        verify(restTemplate).getForEntity(testApiUrl + "/api/tags", String.class);
    }

    @Test
    void listModels_WithFailedResponse_ShouldReturnFailure() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("List failed"));

        // When
        Map<String, Object> result = modelManagementService.listModels();

        // Then
        assertNotNull(result);
        assertEquals("failed", result.get("status"));
        assertTrue(result.containsKey("error"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaMetrics).recordFailure();
    }

    @Test
    void checkModelAvailability_WithModelAvailable_ShouldReturnTrue() {
        // Given
        String modelsResponse = "{\"models\":[{\"name\":\"" + testModelName + "\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(modelsResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = modelManagementService.checkModelAvailability(testModelName);

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertTrue((Boolean) result.get("available"));
        assertTrue(result.containsKey("timestamp"));
    }

    @Test
    void checkModelAvailability_WithModelNotAvailable_ShouldReturnFalse() {
        // Given
        String modelsResponse = "{\"models\":[{\"name\":\"other-model\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(modelsResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = modelManagementService.checkModelAvailability(testModelName);

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertFalse((Boolean) result.get("available"));
        assertTrue(result.containsKey("timestamp"));
    }

    @Test
    void checkModelAvailability_WithException_ShouldReturnFalse() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Check failed"));

        // When
        Map<String, Object> result = modelManagementService.checkModelAvailability(testModelName);

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertFalse((Boolean) result.get("available"));
        assertTrue(result.containsKey("timestamp"));
    }

    @Test
    void ensureModelAvailable_WithModelAlreadyAvailable_ShouldReturnAvailable() {
        // Given
        String modelsResponse = "{\"models\":[{\"name\":\"" + testModelName + "\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(modelsResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = modelManagementService.ensureModelAvailable();

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertEquals("available", result.get("status"));
        assertTrue(result.containsKey("timestamp"));
    }

    @Test
    void ensureModelAvailable_WithModelNotAvailable_ShouldDownloadModel() {
        // Given
        String modelsResponse = "{\"models\":[{\"name\":\"other-model\"}]}";
        ResponseEntity<String> listResponse = new ResponseEntity<>(modelsResponse, HttpStatus.OK);
        ResponseEntity<String> downloadResponse = new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
        
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(listResponse);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(downloadResponse);

        // When
        Map<String, Object> result = modelManagementService.ensureModelAvailable();

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertEquals("downloaded", result.get("status"));
        assertTrue(result.containsKey("downloadTimeMs"));
        assertTrue(result.containsKey("timestamp"));
    }

    @Test
    void getModelInfo_WithSuccessfulResponse_ShouldReturnModelInfo() {
        // Given
        String modelInfoResponse = "{\"name\":\"" + testModelName + "\",\"size\":1000000}";
        ResponseEntity<String> response = new ResponseEntity<>(modelInfoResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = modelManagementService.getModelInfo(testModelName);

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertEquals(modelInfoResponse, result.get("info"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaMetrics).recordSuccess();
        verify(restTemplate).exchange(
                eq(testApiUrl + "/api/show"),
                any(),
                any(),
                eq(String.class)
        );
    }

    @Test
    void getModelInfo_WithFailedResponse_ShouldReturnFailure() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Info failed"));

        // When
        Map<String, Object> result = modelManagementService.getModelInfo(testModelName);

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertEquals("failed", result.get("status"));
        assertTrue(result.containsKey("error"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaMetrics).recordFailure();
    }

    @Test
    void initialize_WithServiceAvailable_ShouldEnsureModelAvailable() {
        // Given
        when(ollamaHealthService.isServiceAvailable()).thenReturn(true);
        String modelsResponse = "{\"models\":[{\"name\":\"" + testModelName + "\"}]}";
        ResponseEntity<String> response = new ResponseEntity<>(modelsResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // When
        Map<String, Object> result = modelManagementService.initialize();

        // Then
        assertNotNull(result);
        assertEquals(testModelName, result.get("model"));
        assertEquals("available", result.get("status"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaHealthService).isServiceAvailable();
    }

    @Test
    void initialize_WithServiceNotAvailable_ShouldReturnFailure() {
        // Given
        when(ollamaHealthService.isServiceAvailable()).thenReturn(false);

        // When
        Map<String, Object> result = modelManagementService.initialize();

        // Then
        assertNotNull(result);
        assertEquals("failed", result.get("status"));
        assertEquals("Ollama service not available", result.get("error"));
        assertTrue(result.containsKey("timestamp"));

        verify(ollamaHealthService).isServiceAvailable();
    }

    @Test
    void initialize_WithException_ShouldReturnFailure() {
        // Given
        when(ollamaHealthService.isServiceAvailable()).thenThrow(new RuntimeException("Service check failed"));

        // When
        Map<String, Object> result = modelManagementService.initialize();

        // Then
        assertNotNull(result);
        assertEquals("failed", result.get("status"));
        assertTrue(result.containsKey("error"));
        assertTrue(result.containsKey("timestamp"));
    }
}
