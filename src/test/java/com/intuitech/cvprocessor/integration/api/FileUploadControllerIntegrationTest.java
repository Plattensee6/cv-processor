package com.intuitech.cvprocessor.integration.api;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.infrastructure.repository.CVProcessingRequestRepository;
import com.intuitech.cvprocessor.util.MockDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for FileUploadController
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("FileUploadController Integration Tests")
class FileUploadControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CVProcessingRequestRepository cvProcessingRequestRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        cvProcessingRequestRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully upload valid PDF file")
    void shouldSuccessfullyUploadValidPdfFile() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createValidPdfFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.fileName").value("john-doe-cv.pdf"))
                .andExpect(jsonPath("$.contentType").value("application/pdf"))
                .andExpect(jsonPath("$.status").value("UPLOADED"))
                .andExpect(jsonPath("$.message").value("File uploaded and parsed successfully"));

        // Verify database
        assertThat(cvProcessingRequestRepository.findAll()).hasSize(1);
        CVProcessingRequest savedRequest = cvProcessingRequestRepository.findAll().get(0);
        assertThat(savedRequest.getFileName()).isEqualTo("john-doe-cv.pdf");
        assertThat(savedRequest.getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.UPLOADED);
    }

    @Test
    @DisplayName("Should successfully upload valid DOC file")
    void shouldSuccessfullyUploadValidDocFile() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createValidDocFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.fileName").value("jane-smith-cv.doc"))
                .andExpect(jsonPath("$.contentType").value("application/msword"))
                .andExpect(jsonPath("$.status").value("UPLOADED"));

        // Verify database
        assertThat(cvProcessingRequestRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Should successfully upload valid DOCX file")
    void shouldSuccessfullyUploadValidDocxFile() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createValidDocxFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.fileName").value("bob-wilson-cv.docx"))
                .andExpect(jsonPath("$.contentType").value("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .andExpect(jsonPath("$.status").value("UPLOADED"));

        // Verify database
        assertThat(cvProcessingRequestRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Should return error for invalid file type")
    void shouldReturnErrorForInvalidFileType() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createInvalidFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("File validation failed")));

        // Verify database - no request should be saved
        assertThat(cvProcessingRequestRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should return error for empty file")
    void shouldReturnErrorForEmptyFile() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createEmptyFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        // Verify database - no request should be saved
        assertThat(cvProcessingRequestRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should return error for large file")
    void shouldReturnErrorForLargeFile() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createLargeFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        // Verify database - no request should be saved
        assertThat(cvProcessingRequestRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should return error when no file provided")
    void shouldReturnErrorWhenNoFileProvided() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        // Verify database - no request should be saved
        assertThat(cvProcessingRequestRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should successfully get processing request by ID")
    void shouldSuccessfullyGetProcessingRequestById() throws Exception {
        // Given
        CVProcessingRequest request = MockDataFactory.createPendingRequest();
        request.setFileName("test-cv.pdf");
        CVProcessingRequest savedRequest = cvProcessingRequestRepository.save(request);

        // When & Then
        mockMvc.perform(get("/api/cv/status/{requestId}", savedRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(savedRequest.getId()))
                .andExpect(jsonPath("$.fileName").value("test-cv.pdf"))
                .andExpect(jsonPath("$.status").value("UPLOADED"));
    }

    @Test
    @DisplayName("Should return error when processing request not found")
    void shouldReturnErrorWhenProcessingRequestNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/cv/status/{requestId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Processing request not found")));
    }

    @Test
    @DisplayName("Should handle multiple file uploads")
    void shouldHandleMultipleFileUploads() throws Exception {
        // Given
        MockMultipartFile file1 = (MockMultipartFile) MockDataFactory.createValidPdfFile();
        MockMultipartFile file2 = (MockMultipartFile) MockDataFactory.createValidDocFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file1)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        // Verify database
        assertThat(cvProcessingRequestRepository.findAll()).hasSize(2);
        assertThat(cvProcessingRequestRepository.findAll())
                .extracting(CVProcessingRequest::getFileName)
                .containsExactlyInAnyOrder("john-doe-cv.pdf", "jane-smith-cv.doc");
    }

    @Test
    @DisplayName("Should return proper error response format")
    void shouldReturnProperErrorResponseFormat() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createInvalidFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/cv/upload"));
    }

    @Test
    @DisplayName("Should return proper success response format")
    void shouldReturnProperSuccessResponseFormat() throws Exception {
        // Given
        MockMultipartFile file = (MockMultipartFile) MockDataFactory.createValidPdfFile();

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.fileName").exists())
                .andExpect(jsonPath("$.contentType").exists())
                .andExpect(jsonPath("$.fileSize").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.uploadedAt").exists())
                .andExpect(jsonPath("$.parsedText").exists());
    }
}
