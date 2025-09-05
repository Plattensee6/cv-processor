package com.intuitech.cvprocessor.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for parsing document content
 * 
 * Handles extraction of text content from various document formats
 * including PDF, DOC, and DOCX files.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentParsingService {

    private final Tika tika = new Tika();

    /**
     * Parse document content from uploaded file
     * 
     * @param file the uploaded file
     * @return extracted text content
     * @throws DocumentParsingException if parsing fails
     */
    public String parseDocument(MultipartFile file) throws DocumentParsingException {
        log.debug("Parsing document: {}", file.getOriginalFilename());
        
        try {
            // Validate file type
            String detectedType = tika.detect(file.getBytes());
            log.debug("Detected file type: {}", detectedType);
            
            // Parse content
            String content = tika.parseToString(file.getInputStream());
            
            if (content == null || content.trim().isEmpty()) {
                throw new DocumentParsingException("No text content found in document");
            }
            
            log.debug("Successfully parsed document, content length: {}", content.length());
            return content.trim();
            
        } catch (IOException e) {
            log.error("IO error while parsing document: {}", e.getMessage());
            throw new DocumentParsingException("Failed to read document content", e);
        } catch (TikaException e) {
            log.error("Tika error while parsing document: {}", e.getMessage());
            throw new DocumentParsingException("Failed to parse document format", e);
        }
    }

    /**
     * Custom exception for document parsing errors
     */
    public static class DocumentParsingException extends Exception {
        public DocumentParsingException(String message) {
            super(message);
        }

        public DocumentParsingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
