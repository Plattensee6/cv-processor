package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.repository.CVProcessingRequestRepository;
import com.intuitech.cvprocessor.infrastructure.repository.ExtractedFieldsRepository;
import com.intuitech.cvprocessor.infrastructure.service.HuggingFaceFieldExtractor;
import com.intuitech.cvprocessor.infrastructure.service.OllamaFieldExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for processing CV documents
 * 
 * Orchestrates the complete CV processing pipeline including field extraction.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CVProcessingService {

    private final CVProcessingRequestRepository cvProcessingRequestRepository;
    private final ExtractedFieldsRepository extractedFieldsRepository;
    private final OllamaFieldExtractor ollamaFieldExtractor;
    private final HuggingFaceFieldExtractor huggingFaceFieldExtractor;

    /**
     * Process CV document and extract fields
     * 
     * @param requestId the processing request ID
     * @return updated processing request
     * @throws CVProcessingException if processing fails
     */
    @Transactional
    public CVProcessingRequest processCV(Long requestId) throws CVProcessingException {
        log.info("Starting CV processing for request ID: {}", requestId);

        // Get processing request
        CVProcessingRequest request = cvProcessingRequestRepository.findById(requestId)
                .orElseThrow(() -> new CVProcessingException("Processing request not found: " + requestId));

        try {
            // Update status to extracting
            request.setStatus(CVProcessingRequest.ProcessingStatus.EXTRACTING);
            cvProcessingRequestRepository.save(request);

            // Extract fields using Ollama (with HuggingFace fallback)
            ExtractedFields extractedFields;
            try {
                log.info("Attempting field extraction with Ollama");
                extractedFields = ollamaFieldExtractor.extractFields(request.getParsedText());
            } catch (Exception e) {
                log.warn("Ollama extraction failed, falling back to HuggingFace: {}", e.getMessage());
                extractedFields = huggingFaceFieldExtractor.extractFields(request.getParsedText());
            }

            // Link extracted fields to processing request
            extractedFields.setCvProcessingRequest(request);

            // Save extracted fields
            extractedFieldsRepository.save(extractedFields);

            // Update status to completed
            request.setStatus(CVProcessingRequest.ProcessingStatus.COMPLETED);
            cvProcessingRequestRepository.save(request);

            log.info("Successfully processed CV for request ID: {}", requestId);
            return request;

        } catch (HuggingFaceFieldExtractor.FieldExtractionException e) {
            log.error("Field extraction failed for request ID {}: {}", requestId, e.getMessage());
            
            // Update status to failed
            request.setStatus(CVProcessingRequest.ProcessingStatus.FAILED);
            request.setErrorMessage("Field extraction failed: " + e.getMessage());
            cvProcessingRequestRepository.save(request);
            
            throw new CVProcessingException("Field extraction failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during CV processing for request ID {}: {}", requestId, e.getMessage(), e);
            
            // Update status to failed
            request.setStatus(CVProcessingRequest.ProcessingStatus.FAILED);
            request.setErrorMessage("Unexpected error: " + e.getMessage());
            cvProcessingRequestRepository.save(request);
            
            throw new CVProcessingException("Unexpected error during CV processing", e);
        }
    }

    /**
     * Get processing request with extracted fields
     * 
     * @param requestId the request ID
     * @return processing request with extracted fields
     * @throws CVProcessingException if request not found
     */
    public CVProcessingRequest getProcessingRequestWithFields(Long requestId) throws CVProcessingException {
        log.debug("Retrieving processing request with fields for ID: {}", requestId);

        return cvProcessingRequestRepository.findById(requestId)
                .orElseThrow(() -> new CVProcessingException("Processing request not found: " + requestId));
    }

    /**
     * Custom exception for CV processing errors
     */
    public static class CVProcessingException extends Exception {
        public CVProcessingException(String message) {
            super(message);
        }

        public CVProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
