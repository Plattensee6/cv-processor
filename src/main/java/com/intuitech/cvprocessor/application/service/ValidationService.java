package com.intuitech.cvprocessor.application.service;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.domain.model.ValidationResult;
import com.intuitech.cvprocessor.domain.validator.*;
import com.intuitech.cvprocessor.infrastructure.repository.ValidationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for validating extracted CV fields
 * 
 * Orchestrates validation of all extracted fields using domain validators.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final WorkExperienceValidator workExperienceValidator;
    private final SkillsValidator skillsValidator;
    private final LanguagesValidator languagesValidator;
    private final ProfileValidator profileValidator;
    private final ValidationResultRepository validationResultRepository;

    /**
     * Validate extracted fields
     * 
     * @param extractedFields the extracted fields to validate
     * @return validation result entity
     * @throws ValidationException if validation fails
     */
    @Transactional
    public ValidationResult validateFields(ExtractedFields extractedFields) throws ValidationException {
        log.info("Starting validation for extracted fields ID: {}", extractedFields.getId());

        try {
            // Validate work experience
            WorkExperienceValidator.ValidationResult workExpResult = 
                workExperienceValidator.validate(extractedFields);

            // Validate skills
            SkillsValidator.ValidationResult skillsResult = 
                skillsValidator.validate(extractedFields);

            // Validate languages
            LanguagesValidator.ValidationResult languagesResult = 
                languagesValidator.validate(extractedFields);

            // Validate profile
            ProfileValidator.ValidationResult profileResult = 
                profileValidator.validate(extractedFields);

            // Create validation result entity
            ValidationResult validationResult = ValidationResult.builder()
                    .extractedFields(extractedFields)
                    .workExperienceValid(workExpResult.isValid())
                    .workExperienceMessage(workExpResult.getMessage())
                    .skillsValid(skillsResult.isValid())
                    .skillsMessage(skillsResult.getMessage())
                    .languagesValid(languagesResult.isValid())
                    .languagesMessage(languagesResult.getMessage())
                    .profileValid(profileResult.isValid())
                    .profileMessage(profileResult.getMessage())
                    .overallValid(workExpResult.isValid() && skillsResult.isValid() && 
                                languagesResult.isValid() && profileResult.isValid())
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
