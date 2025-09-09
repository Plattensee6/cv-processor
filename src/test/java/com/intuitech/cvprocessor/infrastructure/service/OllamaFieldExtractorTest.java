package com.intuitech.cvprocessor.infrastructure.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.config.OllamaConfig;
import com.intuitech.cvprocessor.infrastructure.monitoring.OllamaMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OllamaFieldExtractor
 */
@ExtendWith(MockitoExtension.class)
class OllamaFieldExtractorTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OllamaConfig ollamaConfig;

    @Mock
    private PromptBuilder promptBuilder;

    @SuppressWarnings("unused")
    private ObjectMapper objectMapper; // Injected into OllamaFieldExtractor via @InjectMocks

    @Mock
    private OllamaMetrics ollamaMetrics;

    private OllamaFieldExtractor ollamaFieldExtractor;

    private final String testDocumentText = "John Doe, Software Engineer with 2 years experience in Java and Python.";
    private final String testPrompt = "Extract fields from CV: " + testDocumentText;
    private final String testOllamaResponse = "{\"response\":\"{\\\"workExperience\\\":{\\\"years\\\":2,\\\"details\\\":\\\"Software Engineer\\\"},\\\"skills\\\":[\\\"Java\\\",\\\"Python\\\"],\\\"languages\\\":[\\\"English\\\"],\\\"profile\\\":\\\"Experienced software engineer\\\"}\"}";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        when(ollamaConfig.getModel()).thenReturn("llama3.2:3b");
        when(ollamaConfig.getModelUrl()).thenReturn("http://localhost:11434/api/generate");
        when(promptBuilder.buildFieldExtractionPrompt(testDocumentText)).thenReturn(testPrompt);
        
        // Manually create OllamaFieldExtractor instance
        ollamaFieldExtractor = new OllamaFieldExtractor(
            restTemplate, 
            ollamaConfig, 
            promptBuilder, 
            objectMapper, 
            ollamaMetrics
        );
    }

    @Test
    void extractFields_WithValidResponse_ShouldReturnExtractedFields() throws Exception {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>(testOllamaResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When
        ExtractedFields result = ollamaFieldExtractor.extractFields(testDocumentText);

        // Then
        assertNotNull(result);
        verify(ollamaMetrics).incrementConcurrentRequests();
        verify(ollamaMetrics).recordSuccess();
        verify(ollamaMetrics).recordResponseTime(anyLong());
        verify(ollamaMetrics).decrementConcurrentRequests();
        verify(restTemplate).exchange(
                eq("http://localhost:11434/api/generate"),
                any(),
                any(),
                eq(String.class)
        );
    }

    @Test
    void extractFields_WithEmptyResponse_ShouldThrowException() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When & Then
        assertThrows(OllamaFieldExtractor.FieldExtractionException.class, () -> {
            ollamaFieldExtractor.extractFields(testDocumentText);
        });

        verify(ollamaMetrics).incrementConcurrentRequests();
        verify(ollamaMetrics).recordFailure();
        verify(ollamaMetrics).decrementConcurrentRequests();
    }

    @Test
    void extractFields_WithNullResponse_ShouldThrowException() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When & Then
        assertThrows(OllamaFieldExtractor.FieldExtractionException.class, () -> {
            ollamaFieldExtractor.extractFields(testDocumentText);
        });

        verify(ollamaMetrics).incrementConcurrentRequests();
        verify(ollamaMetrics).recordFailure();
        verify(ollamaMetrics).decrementConcurrentRequests();
    }

    @Test
    void extractFields_WithRestTemplateException_ShouldThrowException() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // When & Then
        assertThrows(OllamaFieldExtractor.FieldExtractionException.class, () -> {
            ollamaFieldExtractor.extractFields(testDocumentText);
        });

        verify(ollamaMetrics).incrementConcurrentRequests();
        verify(ollamaMetrics).recordFailure();
        verify(ollamaMetrics).decrementConcurrentRequests();
    }

    @Test
    void extractFields_WithInvalidJsonResponse_ShouldThrowException() {
        // Given
        String invalidJsonResponse = "{\"response\":\"invalid json\"}";
        ResponseEntity<String> response = new ResponseEntity<>(invalidJsonResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When & Then
        assertThrows(OllamaFieldExtractor.FieldExtractionException.class, () -> {
            ollamaFieldExtractor.extractFields(testDocumentText);
        });

        verify(ollamaMetrics).incrementConcurrentRequests();
        verify(ollamaMetrics).recordFailure();
        verify(ollamaMetrics).decrementConcurrentRequests();
    }

    @Test
    void extractFields_WithMissingResponseField_ShouldThrowException() {
        // Given
        String invalidResponse = "{\"error\":\"No response field\"}";
        ResponseEntity<String> response = new ResponseEntity<>(invalidResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When & Then
        assertThrows(OllamaFieldExtractor.FieldExtractionException.class, () -> {
            ollamaFieldExtractor.extractFields(testDocumentText);
        });

        verify(ollamaMetrics).incrementConcurrentRequests();
        verify(ollamaMetrics).recordFailure();
        verify(ollamaMetrics).decrementConcurrentRequests();
    }

    @Test
    void extractFields_ShouldCallCorrectOllamaApi() throws Exception {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>(testOllamaResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When
        ollamaFieldExtractor.extractFields(testDocumentText);

        // Then
        verify(restTemplate).exchange(
                eq("http://localhost:11434/api/generate"),
                any(),
                argThat(requestEntity -> {
                    // Verify request payload structure
                    Object body = requestEntity.getBody();
                    return body instanceof java.util.Map &&
                           ((java.util.Map<?, ?>) body).containsKey("model") &&
                           ((java.util.Map<?, ?>) body).containsKey("prompt") &&
                           ((java.util.Map<?, ?>) body).containsKey("stream") &&
                           ((java.util.Map<?, ?>) body).containsKey("options");
                }),
                eq(String.class)
        );
    }

    @Test
    void extractFields_ShouldRecordMetricsCorrectly() throws Exception {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>(testOllamaResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(response);

        // When
        ollamaFieldExtractor.extractFields(testDocumentText);

        // Then
        verify(ollamaMetrics).incrementConcurrentRequests();
        verify(ollamaMetrics).recordSuccess();
        verify(ollamaMetrics).recordResponseTime(anyLong());
        verify(ollamaMetrics).decrementConcurrentRequests();
    }

    @Test
    void extractFields_WithException_ShouldStillDecrementConcurrentRequests() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Test exception"));

        // When & Then
        assertThrows(OllamaFieldExtractor.FieldExtractionException.class, () -> {
            ollamaFieldExtractor.extractFields(testDocumentText);
        });

        // Verify that decrementConcurrentRequests is called even when exception occurs
        verify(ollamaMetrics).decrementConcurrentRequests();
    }
}
