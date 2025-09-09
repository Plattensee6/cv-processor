package com.intuitech.cvprocessor.infrastructure.controller;

import com.intuitech.cvprocessor.application.service.ModelManagementService;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OllamaAdminController
 */
@ExtendWith(MockitoExtension.class)
class OllamaAdminControllerTest {

    @Mock
    private ModelManagementService modelManagementService;

    @Mock
    private OllamaMetrics ollamaMetrics;

    @InjectMocks
    private OllamaAdminController ollamaAdminController;

    private final String testModelName = "llama3.2:3b";

    @BeforeEach
    void setUp() {
        // Setup common mock responses
    }

    @Test
    void downloadModel_WithSuccessfulDownload_ShouldReturnOk() {
        // Given
        Map<String, Object> downloadResult = Map.of(
                "model", testModelName,
                "status", "downloaded",
                "downloadTimeMs", 30000L,
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.downloadModel(testModelName)).thenReturn(downloadResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.downloadModel(testModelName);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testModelName, responseBody.get("model"));
        assertEquals("downloaded", responseBody.get("status"));

        verify(modelManagementService).downloadModel(testModelName);
    }

    @Test
    void downloadModel_WithFailedDownload_ShouldReturnInternalServerError() {
        // Given
        when(modelManagementService.downloadModel(testModelName))
                .thenThrow(new RuntimeException("Download failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.downloadModel(testModelName);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(modelManagementService).downloadModel(testModelName);
    }

    @Test
    void listModels_WithSuccessfulResponse_ShouldReturnOk() {
        // Given
        Map<String, Object> listResult = Map.of(
                "status", "success",
                "models", "[{\"name\":\"llama3.2:3b\"}]",
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.listModels()).thenReturn(listResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.listModels();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("success", responseBody.get("status"));
        assertTrue(responseBody.containsKey("models"));

        verify(modelManagementService).listModels();
    }

    @Test
    void listModels_WithFailedResponse_ShouldReturnInternalServerError() {
        // Given
        when(modelManagementService.listModels())
                .thenThrow(new RuntimeException("List failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.listModels();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(modelManagementService).listModels();
    }

    @Test
    void checkModelStatus_WithModelAvailable_ShouldReturnOk() {
        // Given
        Map<String, Object> statusResult = Map.of(
                "model", testModelName,
                "available", true,
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.checkModelAvailability(testModelName)).thenReturn(statusResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.checkModelStatus(testModelName);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testModelName, responseBody.get("model"));
        assertTrue((Boolean) responseBody.get("available"));

        verify(modelManagementService).checkModelAvailability(testModelName);
    }

    @Test
    void checkModelStatus_WithModelNotAvailable_ShouldReturnOk() {
        // Given
        Map<String, Object> statusResult = Map.of(
                "model", testModelName,
                "available", false,
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.checkModelAvailability(testModelName)).thenReturn(statusResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.checkModelStatus(testModelName);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testModelName, responseBody.get("model"));
        assertFalse((Boolean) responseBody.get("available"));

        verify(modelManagementService).checkModelAvailability(testModelName);
    }

    @Test
    void checkModelStatus_WithException_ShouldReturnInternalServerError() {
        // Given
        when(modelManagementService.checkModelAvailability(testModelName))
                .thenThrow(new RuntimeException("Status check failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.checkModelStatus(testModelName);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(modelManagementService).checkModelAvailability(testModelName);
    }

    @Test
    void initialize_WithSuccessfulInitialization_ShouldReturnOk() {
        // Given
        Map<String, Object> initResult = Map.of(
                "model", testModelName,
                "status", "available",
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.initialize()).thenReturn(initResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.initialize();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(testModelName, responseBody.get("model"));
        assertEquals("available", responseBody.get("status"));

        verify(modelManagementService).initialize();
    }

    @Test
    void initialize_WithFailedInitialization_ShouldReturnInternalServerError() {
        // Given
        when(modelManagementService.initialize())
                .thenThrow(new RuntimeException("Initialization failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.initialize();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(modelManagementService).initialize();
    }

    @Test
    void getMetrics_WithValidMetrics_ShouldReturnOk() {
        // Given
        Map<String, Object> metricsResult = Map.of(
                "totalRequests", 100L,
                "successfulRequests", 95L,
                "failedRequests", 5L,
                "fallbackTriggered", 2L,
                "serviceStatus", "UP",
                "modelStatus", "AVAILABLE",
                "lastResponseTimeMs", 150L,
                "concurrentRequests", 3L,
                "averageResponseTimeMs", 200.0
        );
        when(ollamaMetrics.getMetricsSummary()).thenReturn(metricsResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.getMetrics();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(100L, responseBody.get("totalRequests"));
        assertEquals(95L, responseBody.get("successfulRequests"));
        assertEquals(5L, responseBody.get("failedRequests"));
        assertEquals("UP", responseBody.get("serviceStatus"));
        assertEquals("AVAILABLE", responseBody.get("modelStatus"));

        verify(ollamaMetrics).getMetricsSummary();
    }

    @Test
    void getMetrics_WithException_ShouldReturnInternalServerError() {
        // Given
        when(ollamaMetrics.getMetricsSummary())
                .thenThrow(new RuntimeException("Metrics failed"));

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.getMetrics();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaMetrics).getMetricsSummary();
    }

    @Test
    void resetMetrics_WithSuccessfulReset_ShouldReturnOk() {
        // Given
        doNothing().when(ollamaMetrics).reset();

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.resetMetrics();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Metrics reset successfully", responseBody.get("message"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaMetrics).reset();
    }

    @Test
    void resetMetrics_WithException_ShouldReturnInternalServerError() {
        // Given
        doThrow(new RuntimeException("Reset failed")).when(ollamaMetrics).reset();

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.resetMetrics();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.containsKey("timestamp"));

        verify(ollamaMetrics).reset();
    }

    @Test
    void downloadModel_WithEmptyModelName_ShouldStillCallService() {
        // Given
        String emptyModelName = "";
        Map<String, Object> downloadResult = Map.of(
                "model", emptyModelName,
                "status", "failed",
                "error", "Empty model name",
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.downloadModel(emptyModelName)).thenReturn(downloadResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.downloadModel(emptyModelName);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(emptyModelName, responseBody.get("model"));
        assertEquals("failed", responseBody.get("status"));

        verify(modelManagementService).downloadModel(emptyModelName);
    }

    @Test
    void checkModelStatus_WithSpecialCharactersInModelName_ShouldHandleCorrectly() {
        // Given
        String specialModelName = "llama3.2:3b-instruct";
        Map<String, Object> statusResult = Map.of(
                "model", specialModelName,
                "available", true,
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.checkModelAvailability(specialModelName)).thenReturn(statusResult);

        // When
        ResponseEntity<Map<String, Object>> response = ollamaAdminController.checkModelStatus(specialModelName);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(specialModelName, responseBody.get("model"));
        assertTrue((Boolean) responseBody.get("available"));

        verify(modelManagementService).checkModelAvailability(specialModelName);
    }
}
