package com.intuitech.cvprocessor.infrastructure.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DocumentParsingService
 */
class DocumentParsingServiceTest {

    private DocumentParsingService documentParsingService;

    @BeforeEach
    void setUp() {
        documentParsingService = new DocumentParsingService();
    }

    @Test
    void parseDocument_WithTextContent_ShouldReturnParsedText() throws Exception {
        // Given
        String content = "This is a test CV content with work experience and skills.";
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.txt", 
                "text/plain", 
                content.getBytes()
        );

        // When
        String result = documentParsingService.parseDocument(file);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("test CV content"));
    }

    @Test
    void parseDocument_WithEmptyFile_ShouldThrowException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "empty.txt", 
                "text/plain", 
                new byte[0]
        );

        // When & Then
        assertThrows(DocumentParsingService.DocumentParsingException.class, 
                () -> documentParsingService.parseDocument(file));
    }
}
