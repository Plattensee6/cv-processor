package com.intuitech.cvprocessor.infrastructure.service;

import com.intuitech.cvprocessor.application.service.ModelManagementService;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Unit tests for OllamaModelScheduler
 */
@ExtendWith(MockitoExtension.class)
class OllamaModelSchedulerTest {

    @Mock
    private ModelManagementService modelManagementService;

    @Mock
    private OllamaHealthService ollamaHealthService;

    @Mock
    private OllamaMetrics ollamaMetrics;

    @InjectMocks
    private OllamaModelScheduler ollamaModelScheduler;

    @BeforeEach
    void setUp() {
        // Setup common mock responses
    }

    @Test
    void checkModelAvailability_WithServiceAndModelAvailable_ShouldUpdateMetrics() {
        // Given
        when(ollamaHealthService.isServiceAvailable()).thenReturn(true);
        when(ollamaHealthService.isModelAvailable()).thenReturn(true);

        // When
        ollamaModelScheduler.checkModelAvailability();

        // Then
        verify(ollamaHealthService).isServiceAvailable();
        verify(ollamaHealthService).isModelAvailable();
        verify(ollamaMetrics).updateServiceStatus(true);
        verify(ollamaMetrics).updateModelStatus(true);
        verifyNoInteractions(modelManagementService);
    }

    @Test
    void checkModelAvailability_WithServiceUnavailable_ShouldLogWarning() {
        // Given
        when(ollamaHealthService.isServiceAvailable()).thenReturn(false);
        when(ollamaHealthService.isModelAvailable()).thenReturn(false);

        // When
        ollamaModelScheduler.checkModelAvailability();

        // Then
        verify(ollamaHealthService).isServiceAvailable();
        verify(ollamaHealthService).isModelAvailable();
        verify(ollamaMetrics).updateServiceStatus(false);
        verify(ollamaMetrics).updateModelStatus(false);
        verifyNoInteractions(modelManagementService);
    }

    @Test
    void checkModelAvailability_WithModelUnavailable_ShouldEnsureModelAvailable() {
        // Given
        when(ollamaHealthService.isServiceAvailable()).thenReturn(true);
        when(ollamaHealthService.isModelAvailable()).thenReturn(false);
        
        Map<String, Object> result = Map.of(
                "model", "llama3.2:3b",
                "status", "downloaded",
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.ensureModelAvailable()).thenReturn(result);

        // When
        ollamaModelScheduler.checkModelAvailability();

        // Then
        verify(ollamaHealthService).isServiceAvailable();
        verify(ollamaHealthService).isModelAvailable();
        verify(ollamaMetrics).updateServiceStatus(true);
        verify(ollamaMetrics).updateModelStatus(false);
        verify(modelManagementService).ensureModelAvailable();
    }

    @Test
    void checkModelAvailability_WithException_ShouldLogError() {
        // Given
        when(ollamaHealthService.isServiceAvailable())
                .thenThrow(new RuntimeException("Health check failed"));

        // When
        ollamaModelScheduler.checkModelAvailability();

        // Then
        verify(ollamaHealthService).isServiceAvailable();
        verify(ollamaMetrics).recordFailure();
        verifyNoMoreInteractions(ollamaHealthService);
        verifyNoInteractions(modelManagementService);
    }

    @Test
    void performHealthCheck_WithPositiveResponseTime_ShouldRecordMetrics() {
        // Given
        when(ollamaHealthService.getResponseTime()).thenReturn(150L);

        // When
        ollamaModelScheduler.performHealthCheck();

        // Then
        verify(ollamaHealthService).getResponseTime();
        verify(ollamaMetrics).recordResponseTime(150L);
    }

    @Test
    void performHealthCheck_WithNegativeResponseTime_ShouldNotRecordMetrics() {
        // Given
        when(ollamaHealthService.getResponseTime()).thenReturn(-1L);

        // When
        ollamaModelScheduler.performHealthCheck();

        // Then
        verify(ollamaHealthService).getResponseTime();
        verify(ollamaMetrics, never()).recordResponseTime(anyLong());
    }

    @Test
    void performHealthCheck_WithZeroResponseTime_ShouldNotRecordMetrics() {
        // Given
        when(ollamaHealthService.getResponseTime()).thenReturn(0L);

        // When
        ollamaModelScheduler.performHealthCheck();

        // Then
        verify(ollamaHealthService).getResponseTime();
        verify(ollamaMetrics, never()).recordResponseTime(anyLong());
    }

    @Test
    void performHealthCheck_WithException_ShouldLogDebug() {
        // Given
        when(ollamaHealthService.getResponseTime())
                .thenThrow(new RuntimeException("Response time check failed"));

        // When
        ollamaModelScheduler.performHealthCheck();

        // Then
        verify(ollamaHealthService).getResponseTime();
        verify(ollamaMetrics, never()).recordResponseTime(anyLong());
    }

    @Test
    void checkModelAvailability_WithModelManagementException_ShouldLogError() {
        // Given
        when(ollamaHealthService.isServiceAvailable()).thenReturn(true);
        when(ollamaHealthService.isModelAvailable()).thenReturn(false);
        when(modelManagementService.ensureModelAvailable())
                .thenThrow(new RuntimeException("Model management failed"));

        // When
        ollamaModelScheduler.checkModelAvailability();

        // Then
        verify(ollamaHealthService).isServiceAvailable();
        verify(ollamaHealthService).isModelAvailable();
        verify(ollamaMetrics).updateServiceStatus(true);
        verify(ollamaMetrics).updateModelStatus(false);
        verify(modelManagementService).ensureModelAvailable();
    }
}
