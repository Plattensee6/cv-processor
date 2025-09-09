package com.intuitech.cvprocessor.infrastructure.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileValidationService
 */
class FileValidationServiceTest {

    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        fileValidationService = new FileValidationService();
        // Set test properties using reflection
        ReflectionTestUtils.setField(fileValidationService, "maxFileSize", "5MB");
        ReflectionTestUtils.setField(fileValidationService, "allowedTypes", "application/pdf,text/plain");
    }

    @Test
    void validateFile_WithValidFile_ShouldNotThrowException() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.pdf", 
                "application/pdf", 
                "test content".getBytes()
        );

        // When & Then
        assertDoesNotThrow(() -> fileValidationService.validateFile(file));
    }

    @Test
    void validateFile_WithEmptyFile_ShouldThrowException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "empty.pdf", 
                "application/pdf", 
                new byte[0]
        );

        // When & Then
        assertThrows(FileValidationService.FileValidationException.class, 
                () -> fileValidationService.validateFile(file));
    }

    @Test
    void validateFile_WithInvalidContentType_ShouldThrowException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.txt", 
                "application/zip", 
                "test content".getBytes()
        );

        // When & Then
        assertThrows(FileValidationService.FileValidationException.class, 
                () -> fileValidationService.validateFile(file));
    }

    @Test
    void validateFile_WithInvalidExtension_ShouldThrowException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.zip", 
                "application/pdf", 
                "test content".getBytes()
        );

        // When & Then
        assertThrows(FileValidationService.FileValidationException.class, 
                () -> fileValidationService.validateFile(file));
    }
}
