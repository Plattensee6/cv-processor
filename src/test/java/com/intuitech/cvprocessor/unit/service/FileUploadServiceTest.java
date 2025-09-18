package com.intuitech.cvprocessor.unit.service;

import com.intuitech.cvprocessor.application.dto.FileUploadResponseDTO;
import com.intuitech.cvprocessor.application.service.FileUploadService;
import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.infrastructure.repository.CVProcessingRequestRepository;
import com.intuitech.cvprocessor.infrastructure.service.DocumentParsingService;
import com.intuitech.cvprocessor.infrastructure.service.FileValidationService;
import com.intuitech.cvprocessor.util.MockDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FileUploadService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileUploadService Unit Tests")
class FileUploadServiceTest {

    @Mock
    private FileValidationService fileValidationService;

    @Mock
    private DocumentParsingService documentParsingService;

    @Mock
    private CVProcessingRequestRepository cvProcessingRequestRepository;

    @InjectMocks
    private FileUploadService fileUploadService;

    private MultipartFile validPdfFile;
    private MultipartFile invalidFile;
    private CVProcessingRequest testRequest;

    @BeforeEach
    void setUp() {
        validPdfFile = MockDataFactory.createValidPdfFile();
        invalidFile = MockDataFactory.createInvalidFile();
        testRequest = MockDataFactory.createPendingRequest();
        testRequest.setId(1L);
    }

    @Test
    @DisplayName("Should successfully upload valid file")
    void shouldSuccessfullyUploadValidFile() throws Exception {
        // Given
        String parsedText = MockDataFactory.TestScenarios.VALID_CV_TEXT;
        
        doNothing().when(fileValidationService).validateFile(validPdfFile);
        when(documentParsingService.parseDocument(validPdfFile)).thenReturn(parsedText);
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);

        // When
        FileUploadResponseDTO result = fileUploadService.uploadFile(validPdfFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(1L);
        assertThat(result.getFileName()).isEqualTo("john-doe-cv.pdf");
        assertThat(result.getContentType()).isEqualTo("application/pdf");
        assertThat(result.getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.UPLOADED);
        assertThat(result.getMessage()).isEqualTo("File uploaded and parsed successfully");
        assertThat(result.getParsedText()).isEqualTo(parsedText);

        verify(fileValidationService, times(1)).validateFile(validPdfFile);
        verify(documentParsingService, times(1)).parseDocument(validPdfFile);
        verify(cvProcessingRequestRepository, times(1)).save(any(CVProcessingRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when file validation fails")
    void shouldThrowExceptionWhenFileValidationFails() throws Exception {
        // Given
        doThrow(new FileValidationService.FileValidationException("Invalid file type"))
                .when(fileValidationService).validateFile(invalidFile);

        // When & Then
        assertThatThrownBy(() -> fileUploadService.uploadFile(invalidFile))
                .isInstanceOf(FileUploadService.FileUploadException.class)
                .hasMessage("File validation failed: Invalid file type");

        verify(fileValidationService, times(1)).validateFile(invalidFile);
        verify(documentParsingService, never()).parseDocument(any(MultipartFile.class));
        verify(cvProcessingRequestRepository, never()).save(any(CVProcessingRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when document parsing fails")
    void shouldThrowExceptionWhenDocumentParsingFails() throws Exception {
        // Given
        doNothing().when(fileValidationService).validateFile(validPdfFile);
        when(documentParsingService.parseDocument(validPdfFile))
                .thenThrow(new DocumentParsingService.DocumentParsingException("Failed to parse document"));

        // When & Then
        assertThatThrownBy(() -> fileUploadService.uploadFile(validPdfFile))
                .isInstanceOf(FileUploadService.FileUploadException.class)
                .hasMessage("Document parsing failed: Failed to parse document");

        verify(fileValidationService, times(1)).validateFile(validPdfFile);
        verify(documentParsingService, times(1)).parseDocument(validPdfFile);
        verify(cvProcessingRequestRepository, never()).save(any(CVProcessingRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when IO error occurs")
    void shouldThrowExceptionWhenIOErrorOccurs() throws Exception {
        // Given
        MultipartFile fileWithIOError = mock(MultipartFile.class);
        when(fileWithIOError.getOriginalFilename()).thenReturn("test.pdf");
        when(fileWithIOError.getContentType()).thenReturn("application/pdf");
        when(fileWithIOError.getSize()).thenReturn(1024L);
        when(fileWithIOError.getBytes()).thenThrow(new IOException("IO Error"));

        doNothing().when(fileValidationService).validateFile(fileWithIOError);
        when(documentParsingService.parseDocument(fileWithIOError))
                .thenReturn("parsed text");

        // When & Then
        assertThatThrownBy(() -> fileUploadService.uploadFile(fileWithIOError))
                .isInstanceOf(FileUploadService.FileUploadException.class)
                .hasMessage("Failed to read file content");

        verify(fileValidationService, times(1)).validateFile(fileWithIOError);
        verify(documentParsingService, times(1)).parseDocument(fileWithIOError);
        verify(cvProcessingRequestRepository, never()).save(any(CVProcessingRequest.class));
    }

    @Test
    @DisplayName("Should handle unexpected errors")
    void shouldHandleUnexpectedErrors() throws Exception {
        // Given
        doNothing().when(fileValidationService).validateFile(validPdfFile);
        when(documentParsingService.parseDocument(validPdfFile))
                .thenReturn("parsed text");
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> fileUploadService.uploadFile(validPdfFile))
                .isInstanceOf(FileUploadService.FileUploadException.class)
                .hasMessage("Unexpected error during file upload");

        verify(fileValidationService, times(1)).validateFile(validPdfFile);
        verify(documentParsingService, times(1)).parseDocument(validPdfFile);
        verify(cvProcessingRequestRepository, times(1)).save(any(CVProcessingRequest.class));
    }

    @Test
    @DisplayName("Should create processing request with correct data")
    void shouldCreateProcessingRequestWithCorrectData() throws Exception {
        // Given
        String parsedText = MockDataFactory.TestScenarios.VALID_CV_TEXT;
        
        doNothing().when(fileValidationService).validateFile(validPdfFile);
        when(documentParsingService.parseDocument(validPdfFile)).thenReturn(parsedText);
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);

        // When
        fileUploadService.uploadFile(validPdfFile);

        // Then
        verify(cvProcessingRequestRepository, times(1)).save(argThat(request -> 
                request.getFileName().equals("john-doe-cv.pdf") &&
                request.getContentType().equals("application/pdf") &&
                request.getFileSize() == 1024L &&
                request.getParsedText().equals(parsedText) &&
                request.getStatus() == CVProcessingRequest.ProcessingStatus.UPLOADED
        ));
    }

    @Test
    @DisplayName("Should successfully get processing request by ID")
    void shouldSuccessfullyGetProcessingRequestById() throws Exception {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.of(testRequest));

        // When
        FileUploadResponseDTO result = fileUploadService.getProcessingRequest(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRequestId()).isEqualTo(1L);
        assertThat(result.getFileName()).isEqualTo(testRequest.getFileName());
        assertThat(result.getContentType()).isEqualTo(testRequest.getContentType());
        assertThat(result.getFileSize()).isEqualTo(testRequest.getFileSize());
        assertThat(result.getStatus()).isEqualTo(testRequest.getStatus());

        verify(cvProcessingRequestRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when processing request not found")
    void shouldThrowExceptionWhenProcessingRequestNotFound() {
        // Given
        when(cvProcessingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> fileUploadService.getProcessingRequest(1L))
                .isInstanceOf(FileUploadService.FileUploadException.class)
                .hasMessage("Processing request not found: 1");

        verify(cvProcessingRequestRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should build response with correct data")
    void shouldBuildResponseWithCorrectData() throws Exception {
        // Given
        String parsedText = MockDataFactory.TestScenarios.VALID_CV_TEXT;
        
        doNothing().when(fileValidationService).validateFile(validPdfFile);
        when(documentParsingService.parseDocument(validPdfFile)).thenReturn(parsedText);
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);

        // When
        FileUploadResponseDTO result = fileUploadService.uploadFile(validPdfFile);

        // Then
        assertThat(result.getRequestId()).isEqualTo(testRequest.getId());
        assertThat(result.getFileName()).isEqualTo(testRequest.getFileName());
        assertThat(result.getContentType()).isEqualTo(testRequest.getContentType());
        assertThat(result.getFileSize()).isEqualTo(testRequest.getFileSize());
        assertThat(result.getStatus()).isEqualTo(testRequest.getStatus());
        assertThat(result.getUploadedAt()).isEqualTo(testRequest.getCreatedAt());
        assertThat(result.getParsedText()).isEqualTo(testRequest.getParsedText());
        assertThat(result.getMessage()).isEqualTo("File uploaded and parsed successfully");
    }

    @Test
    @DisplayName("Should handle empty file")
    void shouldHandleEmptyFile() throws Exception {
        // Given
        MultipartFile emptyFile = MockDataFactory.createEmptyFile();
        
        doNothing().when(fileValidationService).validateFile(emptyFile);
        when(documentParsingService.parseDocument(emptyFile)).thenReturn("");
        when(cvProcessingRequestRepository.save(any(CVProcessingRequest.class))).thenReturn(testRequest);

        // When
        FileUploadResponseDTO result = fileUploadService.uploadFile(emptyFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFileSize()).isEqualTo(0L);
        assertThat(result.getParsedText()).isEmpty();

        verify(fileValidationService, times(1)).validateFile(emptyFile);
        verify(documentParsingService, times(1)).parseDocument(emptyFile);
        verify(cvProcessingRequestRepository, times(1)).save(any(CVProcessingRequest.class));
    }
}
