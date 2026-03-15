package com.intuitech.cvprocessor.application.dto;

import com.intuitech.cvprocessor.domain.model.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {
    
    private Long requestId;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private ProcessingStatus status;
    private String message;
    private LocalDateTime uploadedAt;
    private String parsedText;
}
