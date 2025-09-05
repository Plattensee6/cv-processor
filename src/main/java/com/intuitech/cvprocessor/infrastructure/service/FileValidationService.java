package com.intuitech.cvprocessor.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Service for validating uploaded files
 * 
 * Handles validation of file types, sizes, and content.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileValidationService {

    @Value("${file.upload.max-size:10MB}")
    private String maxFileSize;

    @Value("${file.upload.allowed-types:application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document}")
    private String allowedTypes;

    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
        ".pdf", ".doc", ".docx"
    );

    /**
     * Validate uploaded file
     * 
     * @param file the uploaded file
     * @throws FileValidationException if validation fails
     */
    public void validateFile(MultipartFile file) throws FileValidationException {
        log.debug("Validating file: {}", file.getOriginalFilename());

        // Check if file is empty
        if (file.isEmpty()) {
            throw new FileValidationException("File is empty");
        }

        // Check file size
        validateFileSize(file);

        // Check file type
        validateFileType(file);

        // Check file extension
        validateFileExtension(file);

        log.debug("File validation successful: {}", file.getOriginalFilename());
    }

    /**
     * Validate file size
     */
    private void validateFileSize(MultipartFile file) throws FileValidationException {
        long maxSizeBytes = parseFileSize(maxFileSize);
        
        if (file.getSize() > maxSizeBytes) {
            throw new FileValidationException(
                String.format("File size %d bytes exceeds maximum allowed size %d bytes", 
                    file.getSize(), maxSizeBytes)
            );
        }
    }

    /**
     * Validate file type
     */
    private void validateFileType(MultipartFile file) throws FileValidationException {
        String contentType = file.getContentType();
        
        if (contentType == null) {
            throw new FileValidationException("File content type is null");
        }

        List<String> allowedTypesList = Arrays.asList(allowedTypes.split(","));
        
        if (!allowedTypesList.contains(contentType)) {
            throw new FileValidationException(
                String.format("File type %s is not allowed. Allowed types: %s", 
                    contentType, allowedTypes)
            );
        }
    }

    /**
     * Validate file extension
     */
    private void validateFileExtension(MultipartFile file) throws FileValidationException {
        String fileName = file.getOriginalFilename();
        
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new FileValidationException("File name is null or empty");
        }

        String extension = getFileExtension(fileName).toLowerCase();
        
        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new FileValidationException(
                String.format("File extension %s is not supported. Supported extensions: %s", 
                    extension, SUPPORTED_EXTENSIONS)
            );
        }
    }

    /**
     * Parse file size string to bytes
     */
    private long parseFileSize(String sizeString) {
        sizeString = sizeString.trim().toUpperCase();
        
        if (sizeString.endsWith("KB")) {
            return Long.parseLong(sizeString.substring(0, sizeString.length() - 2)) * 1024;
        } else if (sizeString.endsWith("MB")) {
            return Long.parseLong(sizeString.substring(0, sizeString.length() - 2)) * 1024 * 1024;
        } else if (sizeString.endsWith("GB")) {
            return Long.parseLong(sizeString.substring(0, sizeString.length() - 2)) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(sizeString);
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }

    /**
     * Custom exception for file validation errors
     */
    public static class FileValidationException extends Exception {
        public FileValidationException(String message) {
            super(message);
        }
    }
}
