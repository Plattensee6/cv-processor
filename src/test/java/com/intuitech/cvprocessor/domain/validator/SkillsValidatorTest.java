package com.intuitech.cvprocessor.domain.validator;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SkillsValidator
 */
class SkillsValidatorTest {

    private SkillsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SkillsValidator();
    }

    @Test
    void validate_WithJavaAndLLM_ShouldReturnValid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .skills("Java, Spring Boot, LLM, Python")
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertTrue(result.isValid());
        assertTrue(result.getMessage().contains("required Java and LLM"));
    }

    @Test
    void validate_WithJavaAndAI_ShouldReturnValid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .skills("Java, Spring Boot, AI, Python")
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertTrue(result.isValid());
        assertTrue(result.getMessage().contains("required Java and LLM"));
    }

    @Test
    void validate_WithJavaAndLargeLanguageModel_ShouldReturnValid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .skills("Java, Spring Boot, Large Language Model, Python")
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertTrue(result.isValid());
        assertTrue(result.getMessage().contains("required Java and LLM"));
    }

    @Test
    void validate_WithoutJava_ShouldReturnInvalid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .skills("Python, LLM, Spring Boot")
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("must include Java"));
    }

    @Test
    void validate_WithoutLLM_ShouldReturnInvalid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .skills("Java, Spring Boot, Python")
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("must include LLM"));
    }

    @Test
    void validate_WithNullSkills_ShouldReturnInvalid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .skills(null)
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void validate_WithEmptySkills_ShouldReturnInvalid() {
        // Given
        ExtractedFields fields = ExtractedFields.builder()
                .skills("")
                .build();

        // When
        ValidationResultDTO result = validator.validate(fields);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("not found"));
    }
}
