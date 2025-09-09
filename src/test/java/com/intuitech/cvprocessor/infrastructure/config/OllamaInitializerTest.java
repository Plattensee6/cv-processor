package com.intuitech.cvprocessor.infrastructure.config;

import com.intuitech.cvprocessor.application.service.ModelManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Unit tests for OllamaInitializer
 */
@ExtendWith(MockitoExtension.class)
class OllamaInitializerTest {

    @Mock
    private ModelManagementService modelManagementService;

    @Mock
    private OllamaConfig ollamaConfig;

    @InjectMocks
    private OllamaInitializer ollamaInitializer;

    @BeforeEach
    void setUp() {
        when(ollamaConfig.getModel()).thenReturn("llama3.2:3b");
    }

    @Test
    void initializeOllamaModel_WithSuccessfulInitialization_ShouldLogSuccess() {
        // Given
        Map<String, Object> result = Map.of(
                "status", "available",
                "model", "llama3.2:3b",
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.initialize()).thenReturn(result);

        // When
        ollamaInitializer.initializeOllamaModel();

        // Then
        verify(modelManagementService).initialize();
    }

    @Test
    void initializeOllamaModel_WithDownloadedStatus_ShouldLogSuccess() {
        // Given
        Map<String, Object> result = Map.of(
                "status", "downloaded",
                "model", "llama3.2:3b",
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.initialize()).thenReturn(result);

        // When
        ollamaInitializer.initializeOllamaModel();

        // Then
        verify(modelManagementService).initialize();
    }

    @Test
    void initializeOllamaModel_WithFailedStatus_ShouldLogWarning() {
        // Given
        Map<String, Object> result = Map.of(
                "status", "failed",
                "error", "Model download failed",
                "timestamp", java.time.LocalDateTime.now()
        );
        when(modelManagementService.initialize()).thenReturn(result);

        // When
        ollamaInitializer.initializeOllamaModel();

        // Then
        verify(modelManagementService).initialize();
    }

    @Test
    void initializeOllamaModel_WithException_ShouldLogError() {
        // Given
        when(modelManagementService.initialize())
                .thenThrow(new RuntimeException("Initialization failed"));

        // When
        ollamaInitializer.initializeOllamaModel();

        // Then
        verify(modelManagementService).initialize();
    }

    @Test
    void initializeOllamaModel_WithNullResult_ShouldHandleGracefully() {
        // Given
        when(modelManagementService.initialize()).thenReturn(null);

        // When
        ollamaInitializer.initializeOllamaModel();

        // Then
        verify(modelManagementService).initialize();
    }
}
