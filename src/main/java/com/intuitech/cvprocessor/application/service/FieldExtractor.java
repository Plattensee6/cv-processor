package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;

/**
 * Interface for extracting fields from CV text using various LLM providers
 * 
 * This interface abstracts the field extraction logic, allowing for different
 * implementations (Ollama, HuggingFace, OpenAI, etc.) without tight coupling.
 */
public interface FieldExtractor {
    
    /**
     * Extract fields from CV text
     * 
     * @param cvText the parsed CV text content
     * @return extracted fields containing work experience, skills, languages, etc.
     * @throws FieldExtractionException if extraction fails
     */
    ExtractedFields extractFields(String cvText) throws FieldExtractionException;
    
    /**
     * Check if the extractor is available and ready to process requests
     * 
     * @return true if the extractor is available, false otherwise
     */
    boolean isAvailable();
    
    /**
     * Get the name of the extractor implementation
     * 
     * @return the extractor name (e.g., "Ollama", "HuggingFace")
     */
    String getExtractorName();
    
    /**
     * Exception thrown when field extraction fails
     */
    class FieldExtractionException extends Exception {
        public FieldExtractionException(String message) {
            super(message);
        }
        
        public FieldExtractionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
