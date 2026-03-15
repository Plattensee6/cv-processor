package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.application.dto.ProcessingResponseDTO;
import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.domain.model.ValidationResult;
import com.intuitech.cvprocessor.feature.cvprocessing.CVProcessingService;
import com.intuitech.cvprocessor.feature.cvprocessing.repository.CVProcessingRequestRepository;
import com.intuitech.cvprocessor.feature.cvprocessing.repository.ExtractedFieldsRepository;
import com.intuitech.cvprocessor.feature.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for complete CV processing pipeline
 * 
 * Orchestrates the entire CV processing workflow from upload to validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompleteProcessingService {

    private final CVProcessingService cvProcessingService;
    private final ValidationService validationService;
    private final CVProcessingRequestRepository cvProcessingRequestRepository;
    private final ExtractedFieldsRepository extractedFieldsRepository;

    /**
     * Process CV completely - extract fields and validate
     * 
     * @param requestId the processing request ID
     * @return complete processing response
     * @throws CompleteProcessingException if processing fails
     */
    @Transactional
    public ProcessingResponseDTO processCVCompletely(Long requestId) throws CompleteProcessingException {
        log.info("Starting complete CV processing for request ID: {}", requestId);

        try {
            CVProcessingRequest request = cvProcessingService.processCV(requestId);
            ExtractedFields extractedFields = extractedFieldsRepository.findByCvProcessingRequestId(requestId)
                    .orElseThrow(() -> new CompleteProcessingException("No extracted fields found for request: " + requestId));
            ValidationResult validationResult = validationService.validateFields(extractedFields);
            ProcessingResponseDTO response = buildCompleteResponse(request, extractedFields, validationResult);
            log.info("Complete CV processing successful for request ID: {}", requestId);
            return response;
        } catch (CVProcessingService.CVProcessingException e) {
            log.error("CV processing failed for request ID {}: {}", requestId, e.getMessage());
            throw new CompleteProcessingException("CV processing failed", e);
        } catch (ValidationService.ValidationException e) {
            log.error("Validation failed for request ID {}: {}", requestId, e.getMessage());
            throw new CompleteProcessingException("Validation failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during complete processing for request ID {}: {}", requestId, e.getMessage(), e);
            throw new CompleteProcessingException("Unexpected error during complete processing", e);
        }
    }

    /**
     * Get complete processing result
     * 
     * @param requestId the request ID
     * @return complete processing response
     * @throws CompleteProcessingException if not found
     */
    public ProcessingResponseDTO getCompleteProcessingResult(Long requestId) throws CompleteProcessingException {
        log.debug("Retrieving complete processing result for request ID: {}", requestId);

        try {
            // Get processing request
            CVProcessingRequest request = cvProcessingRequestRepository.findById(requestId)
                    .orElseThrow(() -> new CompleteProcessingException("Processing request not found: " + requestId));

            // Get extracted fields
            ExtractedFields extractedFields = extractedFieldsRepository.findByCvProcessingRequestId(requestId)
                    .orElseThrow(() -> new CompleteProcessingException("No extracted fields found for request: " + requestId));

            // Get validation result
            ValidationResult validationResult = validationService.getValidationResult(extractedFields.getId());

            // Build complete response
            return buildCompleteResponse(request, extractedFields, validationResult);

        } catch (ValidationService.ValidationException e) {
            log.error("Failed to retrieve validation result for request ID {}: {}", requestId, e.getMessage());
            throw new CompleteProcessingException("Failed to retrieve validation result", e);
        }
    }

    /**
     * Build complete processing response
     */
    private ProcessingResponseDTO buildCompleteResponse(CVProcessingRequest request, 
                                                       ExtractedFields extractedFields, 
                                                       ValidationResult validationResult) {
        
        // Build extracted fields DTO
        ProcessingResponseDTO.ExtractedFieldsDTO extractedFieldsDTO = ProcessingResponseDTO.ExtractedFieldsDTO.builder()
                .workExperienceYears(extractedFields.getWorkExperienceYears())
                .workExperienceDetails(extractedFields.getWorkExperienceDetails())
                .skills(extractedFields.getSkills())
                .languages(extractedFields.getLanguages())
                .profile(extractedFields.getProfile())
                .extractedAt(extractedFields.getCreatedAt())
                .build();

        // Build validation result DTO
        ProcessingResponseDTO.ValidationResultDTO validationResultDTO = ProcessingResponseDTO.ValidationResultDTO.builder()
                .workExperienceValid(validationResult.getWorkExperienceValid())
                .workExperienceMessage(validationResult.getWorkExperienceMessage())
                .skillsValid(validationResult.getSkillsValid())
                .skillsMessage(validationResult.getSkillsMessage())
                .languagesValid(validationResult.getLanguagesValid())
                .languagesMessage(validationResult.getLanguagesMessage())
                .profileValid(validationResult.getProfileValid())
                .profileMessage(validationResult.getProfileMessage())
                .overallValid(validationResult.getOverallValid())
                .validatedAt(validationResult.getCreatedAt())
                .build();

        // Build complete response
        return ProcessingResponseDTO.builder()
                .requestId(request.getId())
                .fileName(request.getFileName())
                .status(request.getStatus())
                .message("CV processed and validated successfully")
                .processedAt(request.getUpdatedAt())
                .extractedFields(extractedFieldsDTO)
                .validationResult(validationResultDTO)
                .build();
    }

    /**
     * Custom exception for complete processing errors
     */
    public static class CompleteProcessingException extends Exception {
        public CompleteProcessingException(String message) {
            super(message);
        }

        public CompleteProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
