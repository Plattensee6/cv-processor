package com.intuitech.cvprocessor.domain.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for work experience field
 * 
 * Validates that work experience is between 0 and 2 years.
 */
@Component
@Slf4j
public class WorkExperienceValidator implements Validator {
    
    private static final int MIN_YEARS = 0;
    private static final int MAX_YEARS = 2;
    private static final String FIELD_NAME = "workExperience";

    @Override
    public ValidationResultDTO validate(ExtractedFields extractedFields) {
        log.debug("Validating work experience");

        Integer years = extractedFields.getWorkExperienceYears();
        
        if (years == null) {
            return ValidationResultDTO.invalid("Work experience years not found", FIELD_NAME);
        }

        if (years < MIN_YEARS || years > MAX_YEARS) {
            return ValidationResultDTO.invalid(
                String.format("Work experience %d years is not between %d and %d years", 
                    years, MIN_YEARS, MAX_YEARS), FIELD_NAME
            );
        }

        return ValidationResultDTO.valid(
            String.format("Work experience %d years is within valid range (%d-%d years)", 
                years, MIN_YEARS, MAX_YEARS), FIELD_NAME
        );
    }

    @Override
    public String getFieldName() {
        return FIELD_NAME;
    }

    @Override
    public int getPriority() {
        return 10; // High priority
    }
}
