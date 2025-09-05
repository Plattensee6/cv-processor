package com.intuitech.cvprocessor.presentation.controller;

import com.intuitech.cvprocessor.application.dto.FileUploadResponseDTO;
import com.intuitech.cvprocessor.application.service.FileUploadService;
import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for FileUploadController
 */
@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    @Test
    void uploadFile_WithValidFile_ShouldReturnOk() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.pdf", 
                "application/pdf", 
                "test content".getBytes()
        );

        FileUploadResponseDTO response = FileUploadResponseDTO.builder()
                .requestId(1L)
                .fileName("test.pdf")
                .contentType("application/pdf")
                .fileSize(12L)
                .status(CVProcessingRequest.ProcessingStatus.UPLOADED)
                .message("File uploaded successfully")
                .uploadedAt(LocalDateTime.now())
                .parsedText("test content")
                .build();

        when(fileUploadService.uploadFile(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(1))
                .andExpect(jsonPath("$.fileName").value("test.pdf"))
                .andExpect(jsonPath("$.status").value("UPLOADED"));
    }

    @Test
    void uploadFile_WithInvalidFile_ShouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.txt", 
                "text/plain", 
                "test content".getBytes()
        );

        when(fileUploadService.uploadFile(any()))
                .thenThrow(new FileUploadService.FileUploadException("Invalid file type"));

        // When & Then
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File upload failed"));
    }

    @Test
    void getProcessingStatus_WithValidId_ShouldReturnOk() throws Exception {
        // Given
        FileUploadResponseDTO response = FileUploadResponseDTO.builder()
                .requestId(1L)
                .fileName("test.pdf")
                .status(CVProcessingRequest.ProcessingStatus.UPLOADED)
                .build();

        when(fileUploadService.getProcessingRequest(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/cv/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(1))
                .andExpect(jsonPath("$.fileName").value("test.pdf"));
    }

    @Test
    void health_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/cv/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("File Upload Service"));
    }
}
