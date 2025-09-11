package com.intuitech.cvprocessor.unit.service;

import com.intuitech.cvprocessor.application.service.ValidationService;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.domain.model.ValidationResult;
import com.intuitech.cvprocessor.domain.validator.ValidationResultDTO;
import com.intuitech.cvprocessor.domain.validator.ValidatorRegistry;
import com.intuitech.cvprocessor.infrastructure.repository.ValidationResultRepository;
import com.intuitech.cvprocessor.util.MockDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ValidationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidationService Unit Tests")
class ValidationServiceTest {

    @Mock
    private ValidatorRegistry validatorRegistry;

    @Mock
    private ValidationResultRepository validationResultRepository;

    @InjectMocks
    private ValidationService validationService;

    private ExtractedFields validExtractedFields;
    private ExtractedFields invalidExtractedFields;

    @BeforeEach
    void setUp() {
        validExtractedFields = MockDataFactory.createValidExtractedFields();
        validExtractedFields.setId(1L);
        
        invalidExtractedFields = MockDataFactory.createInvalidExtractedFields();
        invalidExtractedFields.setId(2L);
    }

    // Helper method to create validator validation results
    private ValidationResultDTO createValidatorResult(boolean valid, String message, String fieldName) {
        return valid 
            ? ValidationResultDTO.valid(message, fieldName)
            : ValidationResultDTO.invalid(message, fieldName);
    }

    @Test
    @DisplayName("Should successfully validate all fields when all validators pass")
    void shouldSuccessfullyValidateAllFieldsWhenAllValidatorsPass() throws Exception {
        // Given
        Map<String, ValidationResultDTO> validationResults = new HashMap<>();
        validationResults.put("workExperience", createValidatorResult(true, "Work experience is valid", "workExperience"));
        validationResults.put("skills", createValidatorResult(true, "Skills are valid", "skills"));
        validationResults.put("languages", createValidatorResult(true, "Languages are valid", "languages"));
        validationResults.put("profile", createValidatorResult(true, "Profile is valid", "profile"));

        when(validatorRegistry.validateAll(validExtractedFields)).thenReturn(validationResults);

        ValidationResult expectedResult = MockDataFactory.createValidValidationResult();
        when(validationResultRepository.save(any(ValidationResult.class))).thenReturn(expectedResult);

        // When
        ValidationResult result = validationService.validateFields(validExtractedFields);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResult);

        verify(validatorRegistry, times(1)).validateAll(validExtractedFields);
        verify(validationResultRepository, times(1)).save(any(ValidationResult.class));
    }

    @Test
    @DisplayName("Should create validation result with correct overall validity when some validators fail")
    void shouldCreateValidationResultWithCorrectOverallValidityWhenSomeValidatorsFail() throws Exception {
        // Given
        Map<String, ValidationResultDTO> validationResults = new HashMap<>();
        validationResults.put("workExperience", createValidatorResult(true, "Work experience is valid", "workExperience"));
        validationResults.put("skills", createValidatorResult(false, "Skills are invalid", "skills"));
        validationResults.put("languages", createValidatorResult(true, "Languages are valid", "languages"));
        validationResults.put("profile", createValidatorResult(false, "Profile is invalid", "profile"));

        when(validatorRegistry.validateAll(validExtractedFields)).thenReturn(validationResults);

        ValidationResult expectedResult = MockDataFactory.createInvalidValidationResult();
        when(validationResultRepository.save(any(ValidationResult.class))).thenReturn(expectedResult);

        // When
        ValidationResult result = validationService.validateFields(validExtractedFields);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResult);

        verify(validationResultRepository, times(1)).save(argThat(validationResult -> 
                !validationResult.getOverallValid() // Should be false since some validators failed
        ));
    }

    @Test
    @DisplayName("Should handle validation exception from validators")
    void shouldHandleValidationExceptionFromValidators() throws Exception {
        // Given
        when(validatorRegistry.validateAll(validExtractedFields))
                .thenThrow(new RuntimeException("Validator error"));

        // When & Then
        assertThatThrownBy(() -> validationService.validateFields(validExtractedFields))
                .isInstanceOf(ValidationService.ValidationException.class)
                .hasMessage("Validation failed");

        verify(validationResultRepository, never()).save(any(ValidationResult.class));
    }

    @Test
    @DisplayName("Should handle repository save exception")
    void shouldHandleRepositorySaveException() throws Exception {
        // Given
        Map<String, ValidationResultDTO> validationResults = new HashMap<>();
        validationResults.put("workExperience", createValidatorResult(true, "Work experience is valid", "workExperience"));
        validationResults.put("skills", createValidatorResult(true, "Skills are valid", "skills"));
        validationResults.put("languages", createValidatorResult(true, "Languages are valid", "languages"));
        validationResults.put("profile", createValidatorResult(true, "Profile is valid", "profile"));

        when(validatorRegistry.validateAll(validExtractedFields)).thenReturn(validationResults);
        when(validationResultRepository.save(any(ValidationResult.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> validationService.validateFields(validExtractedFields))
                .isInstanceOf(ValidationService.ValidationException.class)
                .hasMessage("Validation failed");
    }

    @Test
    @DisplayName("Should successfully get validation result by extracted fields ID")
    void shouldSuccessfullyGetValidationResultByExtractedFieldsId() throws Exception {
        // Given
        ValidationResult expectedResult = MockDataFactory.createValidValidationResult();
        when(validationResultRepository.findByExtractedFieldsId(1L))
                .thenReturn(Optional.of(expectedResult));

        // When
        ValidationResult result = validationService.getValidationResult(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResult);
        verify(validationResultRepository, times(1)).findByExtractedFieldsId(1L);
    }

    @Test
    @DisplayName("Should throw exception when validation result not found")
    void shouldThrowExceptionWhenValidationResultNotFound() {
        // Given
        when(validationResultRepository.findByExtractedFieldsId(1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> validationService.getValidationResult(1L))
                .isInstanceOf(ValidationService.ValidationException.class)
                .hasMessage("Validation result not found: 1");
    }

    @Test
    @DisplayName("Should call validator registry to validate all fields")
    void shouldCallValidatorRegistryToValidateAllFields() throws Exception {
        // Given
        Map<String, ValidationResultDTO> validationResults = new HashMap<>();
        validationResults.put("workExperience", createValidatorResult(true, "Work experience is valid", "workExperience"));
        validationResults.put("skills", createValidatorResult(true, "Skills are valid", "skills"));
        validationResults.put("languages", createValidatorResult(true, "Languages are valid", "languages"));
        validationResults.put("profile", createValidatorResult(true, "Profile is valid", "profile"));

        when(validatorRegistry.validateAll(validExtractedFields)).thenReturn(validationResults);

        ValidationResult expectedResult = MockDataFactory.createValidValidationResult();
        when(validationResultRepository.save(any(ValidationResult.class))).thenReturn(expectedResult);

        // When
        validationService.validateFields(validExtractedFields);

        // Then
        verify(validatorRegistry, times(1)).validateAll(validExtractedFields);
    }

    @Test
    @DisplayName("Should create validation result with all validator results")
    void shouldCreateValidationResultWithAllValidatorResults() throws Exception {
        // Given
        Map<String, ValidationResultDTO> validationResults = new HashMap<>();
        validationResults.put("workExperience", createValidatorResult(true, "Work experience is valid", "workExperience"));
        validationResults.put("skills", createValidatorResult(false, "Skills are invalid", "skills"));
        validationResults.put("languages", createValidatorResult(true, "Languages are valid", "languages"));
        validationResults.put("profile", createValidatorResult(false, "Profile is invalid", "profile"));

        when(validatorRegistry.validateAll(validExtractedFields)).thenReturn(validationResults);

        ValidationResult expectedResult = MockDataFactory.createValidValidationResult();
        when(validationResultRepository.save(any(ValidationResult.class))).thenReturn(expectedResult);

        // When
        validationService.validateFields(validExtractedFields);

        // Then
        verify(validationResultRepository, times(1)).save(argThat(validationResult -> 
                validationResult.getWorkExperienceValid() == true &&
                validationResult.getWorkExperienceMessage().equals("Work experience is valid") &&
                validationResult.getSkillsValid() == false &&
                validationResult.getSkillsMessage().equals("Skills are invalid") &&
                validationResult.getLanguagesValid() == true &&
                validationResult.getLanguagesMessage().equals("Languages are valid") &&
                validationResult.getProfileValid() == false &&
                validationResult.getProfileMessage().equals("Profile is invalid") &&
                validationResult.getOverallValid() == false
        ));
    }
}
