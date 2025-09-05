package com.intuitech.cvprocessor.application.dto;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for file upload responses
 * 
 * Contains information about the uploaded file and processing status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {
    
    private Long requestId;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private CVProcessingRequest.ProcessingStatus status;
    private String message;
    private LocalDateTime uploadedAt;
    private String parsedText;
}
