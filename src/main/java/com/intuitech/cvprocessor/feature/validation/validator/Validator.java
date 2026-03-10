package com.intuitech.cvprocessor.feature.validation.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.feature.validation.dto.ValidationResultDTO;

/**
 * Common interface for all field validators
 *
 * Provides a unified contract for validating extracted CV fields.
 */
public interface Validator {

    /**
     * Validate a specific field from extracted fields
     *
     * @param extractedFields the extracted fields to validate
     * @return validation result
     */
    ValidationResultDTO validate(ExtractedFields extractedFields);

    /**
     * Get the name of the field this validator validates
     *
     * @return field name
     */
    String getFieldName();

    /**
     * Get the priority of this validator (lower number = higher priority)
     *
     * @return priority number
     */
    default int getPriority() {
        return 100;
    }
}

