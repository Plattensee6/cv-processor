package com.intuitech.cvprocessor.domain.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.feature.validation.dto.ValidationResultDTO;
import com.intuitech.cvprocessor.feature.validation.validator.WorkExperienceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WorkExperienceValidator
 */
class WorkExperienceValidatorTest {

    private WorkExperienceValidator validator;

    @BeforeEach
    void setUp() {
        validator = new WorkExperienceValidator();
    }

    @Test
    void validate_WithValidYears_ShouldReturnValid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .workExperienceYears(1)
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertTrue(result.isValid());
        assertTrue(result.getMessage().contains("within valid range"));
    }

    @Test
    void validate_WithZeroYears_ShouldReturnValid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .workExperienceYears(0)
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertTrue(result.isValid());
    }

    @Test
    void validate_WithTwoYears_ShouldReturnValid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .workExperienceYears(2)
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertTrue(result.isValid());
    }

    @Test
    void validate_WithTooManyYears_ShouldReturnInvalid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .workExperienceYears(5)
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("not between"));
    }

    @Test
    void validate_WithNegativeYears_ShouldReturnInvalid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .workExperienceYears(-1)
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("not between"));
    }

    @Test
    void validate_WithNullYears_ShouldReturnInvalid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .workExperienceYears(null)
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("not found"));
    }
}
