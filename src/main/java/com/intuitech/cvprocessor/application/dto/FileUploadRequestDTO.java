package com.intuitech.cvprocessor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for file upload requests
 * 
 * Contains metadata about the uploaded file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequestDTO {
    
    private String fileName;
    private String contentType;
    private Long fileSize;
    private byte[] content;
}
