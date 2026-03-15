package com.intuitech.cvprocessor.feature.cvprocessing;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.domain.model.ProcessingStatus;
import com.intuitech.cvprocessor.feature.cvprocessing.repository.CVProcessingRequestRepository;
import com.intuitech.cvprocessor.feature.cvprocessing.repository.ExtractedFieldsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVProcessingService {

    private final CVProcessingRequestRepository cvProcessingRequestRepository;
    private final ExtractedFieldsRepository extractedFieldsRepository;
    private final FieldExtractor fieldExtractor;

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
            CVProcessingRequest extractingRequest = buildRequestWithStatus(request, ProcessingStatus.EXTRACTING, null);
            CVProcessingRequest savedExtracting = cvProcessingRequestRepository.save(extractingRequest);

            // Extract fields using configured extractor
            log.info("Extracting fields with {}", fieldExtractor.getExtractorName());
            ExtractedFields extractedFields = fieldExtractor.extractFields(request.getParsedText());

            // Link extracted fields to processing request via builder
            ExtractedFields toSave = ExtractedFields.builder()
                    .cvProcessingRequest(savedExtracting)
                    .workExperienceYears(extractedFields.getWorkExperienceYears())
                    .workExperienceDetails(extractedFields.getWorkExperienceDetails())
                    .skills(extractedFields.getSkills())
                    .languages(extractedFields.getLanguages())
                    .profile(extractedFields.getProfile())
                    .build();
            extractedFieldsRepository.save(toSave);

            CVProcessingRequest completedRequest = buildRequestWithStatus(savedExtracting, ProcessingStatus.COMPLETED, null);
            CVProcessingRequest savedCompleted = cvProcessingRequestRepository.save(completedRequest);

            log.info("Successfully processed CV for request ID: {}", requestId);
            return savedCompleted;

        } catch (FieldExtractor.FieldExtractionException e) {
            log.error("Field extraction failed for request ID {}: {}", requestId, e.getMessage());

            CVProcessingRequest failedRequest = buildRequestWithStatus(request, ProcessingStatus.FAILED, "Field extraction failed: " + e.getMessage());
            cvProcessingRequestRepository.save(failedRequest);

            throw new CVProcessingException("Field extraction failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during CV processing for request ID {}: {}", requestId, e.getMessage(), e);

            CVProcessingRequest failedRequest = buildRequestWithStatus(request, ProcessingStatus.FAILED, "Unexpected error: " + e.getMessage());
            cvProcessingRequestRepository.save(failedRequest);

            throw new CVProcessingException("Unexpected error during CV processing", e);
        }
    }

    private CVProcessingRequest buildRequestWithStatus(CVProcessingRequest request,
                                                       ProcessingStatus status,
                                                       String errorMessage) {
        return CVProcessingRequest.builder()
                .id(request.getId())
                .fileName(request.getFileName())
                .contentType(request.getContentType())
                .fileSize(request.getFileSize())
                .originalContent(request.getOriginalContent())
                .parsedText(request.getParsedText())
                .status(status)
                .errorMessage(errorMessage != null ? errorMessage : request.getErrorMessage())
                .createdAt(request.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .extractedFields(request.getExtractedFields())
                .build();
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

    public static class CVProcessingException extends Exception {
        public CVProcessingException(String message) {
            super(message);
        }

        public CVProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

