package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.domain.model.ValidationResult;
import com.intuitech.cvprocessor.domain.validator.ValidationResultDTO;
import com.intuitech.cvprocessor.domain.validator.ValidatorRegistry;
import com.intuitech.cvprocessor.infrastructure.repository.ValidationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service for validating extracted CV fields
 * 
 * Uses ValidatorRegistry to orchestrate validation of all extracted fields.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final ValidatorRegistry validatorRegistry;
    private final ValidationResultRepository validationResultRepository;

    /**
     * Validate extracted fields using all registered validators
     * 
     * @param extractedFields the extracted fields to validate
     * @return validation result entity
     * @throws ValidationException if validation fails
     */
    @Transactional
    public ValidationResult validateFields(ExtractedFields extractedFields) throws ValidationException {
        log.info("Starting validation for extracted fields ID: {}", extractedFields.getId());

        try {
            // Execute all validators
            Map<String, ValidationResultDTO> validationResults =
                validatorRegistry.validateAll(extractedFields);

            // Extract results for each field
            ValidationResultDTO workExpResult = validationResults.get("workExperience");
            ValidationResultDTO skillsResult = validationResults.get("skills");
            ValidationResultDTO languagesResult = validationResults.get("languages");
            ValidationResultDTO profileResult = validationResults.get("profile");

            // Create validation result entity
            ValidationResult validationResult = ValidationResult.builder()
                    .extractedFields(extractedFields)
                    .workExperienceValid(workExpResult != null ? workExpResult.isValid() : false)
                    .workExperienceMessage(workExpResult != null ? workExpResult.getMessage() : "Validator not found")
                    .skillsValid(skillsResult != null ? skillsResult.isValid() : false)
                    .skillsMessage(skillsResult != null ? skillsResult.getMessage() : "Validator not found")
                    .languagesValid(languagesResult != null ? languagesResult.isValid() : false)
                    .languagesMessage(languagesResult != null ? languagesResult.getMessage() : "Validator not found")
                    .profileValid(profileResult != null ? profileResult.isValid() : false)
                    .profileMessage(profileResult != null ? profileResult.getMessage() : "Validator not found")
                    .overallValid(validationResults.values().stream().allMatch(ValidationResultDTO::isValid))
                    .build();

            // Save validation result
            ValidationResult savedResult = validationResultRepository.save(validationResult);

            log.info("Validation completed for extracted fields ID: {}, overall valid: {}", 
                extractedFields.getId(), savedResult.getOverallValid());

            return savedResult;

        } catch (Exception e) {
            log.error("Validation failed for extracted fields ID {}: {}", 
                extractedFields.getId(), e.getMessage(), e);
            throw new ValidationException("Validation failed", e);
        }
    }

    /**
     * Get validation result by extracted fields ID
     * 
     * @param extractedFieldsId the extracted fields ID
     * @return validation result
     * @throws ValidationException if not found
     */
    public ValidationResult getValidationResult(Long extractedFieldsId) throws ValidationException {
        log.debug("Retrieving validation result for extracted fields ID: {}", extractedFieldsId);

        return validationResultRepository.findByExtractedFieldsId(extractedFieldsId)
                .orElseThrow(() -> new ValidationException("Validation result not found: " + extractedFieldsId));
    }

    /**
     * Get all registered validators
     * 
     * @return list of validators
     */
    public List<com.intuitech.cvprocessor.domain.validator.Validator> getValidators() {
        return validatorRegistry.getValidators();
    }

    /**
     * Custom exception for validation errors
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
