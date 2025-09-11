package com.intuitech.cvprocessor.unit.service;

import com.intuitech.cvprocessor.application.service.CVProcessingService;
import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.repository.CVProcessingRequestRepository;
import com.intuitech.cvprocessor.infrastructure.repository.ExtractedFieldsRepository;
import com.intuitech.cvprocessor.application.service.FieldExtractor;
import com.intuitech.cvprocessor.util.MockDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CVProcessingService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CVProcessingService Unit Tests")
class CVProcessingServiceTest {

    @Mock
    private CVProcessingRequestRepository cvProcessingRequestRepository;

    @Mock
    private ExtractedFieldsRepository extractedFieldsRepository;

    @Mock
    private FieldExtractor fieldExtractor;


    @InjectMocks
    private CVProcessingService cvProcessingService;

    private CVProcessingRequest testRequest;
    private ExtractedFields testExtractedFields;

    @BeforeEach
    void setUp() {
        testRequest = MockDataFactory.createPendingRequest();
        testRequest.setId(1L);
        testRequest.setParsedText(MockDataFactory.TestScenarios.VALID_CV_TEXT);
        
        testExtractedFields = MockDataFactory.createValidExtractedFields();
    }

    @Test
    @DisplayName("Should successfully process CV with Ollama")
    void shouldSuccessfullyProcessCVWithOllama() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(fieldExtractor.extractFields(anyString())).thenReturn(testExtractedFields);
        when(fieldExtractor.getExtractorName()).thenReturn("TestExtractor");
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);
        when(extractedFieldsRepository.save(any(ExtractedFields.class))).thenReturn(testExtractedFields);

        // When
        CVProcessingRequest result = cvProcessingService.processCV(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.COMPLETED);
        
        verify(cvProcessingRequestRepository, times(2)).save(any(CVProcessingRequest.class));
        verify(extractedFieldsRepository, times(1)).save(any(ExtractedFields.class));
        verify(fieldExtractor, times(1)).extractFields(anyString());
    }

    @Test
    @DisplayName("Should handle Ollama extraction failure")
    void shouldHandleOllamaExtractionFailure() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(fieldExtractor.extractFields(anyString()))
                .thenThrow(new FieldExtractor.FieldExtractionException("Extractor service unavailable"));

        // When & Then
        assertThatThrownBy(() -> cvProcessingService.processCV(1L))
                .isInstanceOf(CVProcessingService.CVProcessingException.class)
                .hasMessageContaining("Field extraction failed");
        
        verify(cvProcessingRequestRepository, times(2)).save(any(CVProcessingRequest.class));
        verify(fieldExtractor, times(1)).extractFields(anyString());
    }

    @Test
    @DisplayName("Should throw exception when request not found")
    void shouldThrowExceptionWhenRequestNotFound() {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cvProcessingService.processCV(1L))
                .isInstanceOf(CVProcessingService.CVProcessingException.class)
                .hasMessage("Processing request not found: 1");

        verify(cvProcessingRequestRepository, never()).save(any(CVProcessingRequest.class));
        verify(extractedFieldsRepository, never()).save(any(ExtractedFields.class));
    }

    @Test
    @DisplayName("Should handle extractor service failure with proper error handling")
    void shouldHandleExtractorServiceFailureWithProperErrorHandling() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(fieldExtractor.extractFields(anyString()))
                .thenThrow(new FieldExtractor.FieldExtractionException("Extractor service unavailable"));
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);

        // When & Then
        assertThatThrownBy(() -> cvProcessingService.processCV(1L))
                .isInstanceOf(CVProcessingService.CVProcessingException.class)
                .hasMessage("Field extraction failed");

        verify(cvProcessingRequestRepository, times(2)).save(any(CVProcessingRequest.class));
        verify(extractedFieldsRepository, never()).save(any(ExtractedFields.class));
        
        // Verify that the request status was updated to FAILED
        verify(cvProcessingRequestRepository, atLeastOnce()).save(argThat(request -> 
                request.getStatus() == CVProcessingRequest.ProcessingStatus.FAILED &&
                request.getErrorMessage().contains("Field extraction failed")
        ));
    }

    @Test
    @DisplayName("Should handle unexpected errors")
    void shouldHandleUnexpectedErrors() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(fieldExtractor.extractFields(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);

        // When & Then
        assertThatThrownBy(() -> cvProcessingService.processCV(1L))
                .isInstanceOf(CVProcessingService.CVProcessingException.class)
                .hasMessage("Unexpected error during CV processing");

        verify(cvProcessingRequestRepository, times(2)).save(any(CVProcessingRequest.class));
        verify(extractedFieldsRepository, never()).save(any(ExtractedFields.class));
        
        // Verify that the request status was updated to FAILED
        verify(cvProcessingRequestRepository, atLeastOnce()).save(argThat(request -> 
                request.getStatus() == CVProcessingRequest.ProcessingStatus.FAILED &&
                request.getErrorMessage().contains("Unexpected error")
        ));
    }

    @Test
    @DisplayName("Should update status to EXTRACTING before processing")
    void shouldUpdateStatusToExtractingBeforeProcessing() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(fieldExtractor.extractFields(anyString())).thenReturn(testExtractedFields);
        when(fieldExtractor.getExtractorName()).thenReturn("TestExtractor");
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);
        when(extractedFieldsRepository.save(any(ExtractedFields.class))).thenReturn(testExtractedFields);

        // When
        cvProcessingService.processCV(1L);

        // Then
        verify(cvProcessingRequestRepository, times(2)).save(any(CVProcessingRequest.class));
    }

    @Test
    @DisplayName("Should link extracted fields to processing request")
    void shouldLinkExtractedFieldsToProcessingRequest() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(fieldExtractor.extractFields(anyString())).thenReturn(testExtractedFields);
        when(fieldExtractor.getExtractorName()).thenReturn("TestExtractor");
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);
        when(extractedFieldsRepository.save(any(ExtractedFields.class))).thenReturn(testExtractedFields);

        // When
        cvProcessingService.processCV(1L);

        // Then
        verify(extractedFieldsRepository, times(1)).save(argThat(fields -> 
                fields.getCvProcessingRequest() == testRequest
        ));
    }

    @Test
    @DisplayName("Should successfully get processing request with fields")
    void shouldSuccessfullyGetProcessingRequestWithFields() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        // When
        CVProcessingRequest result = cvProcessingService.getProcessingRequestWithFields(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testRequest);
        verify(cvProcessingRequestRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent request")
    void shouldThrowExceptionWhenGettingNonExistentRequest() {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cvProcessingService.getProcessingRequestWithFields(1L))
                .isInstanceOf(CVProcessingService.CVProcessingException.class)
                .hasMessage("Processing request not found: 1");
    }
}
