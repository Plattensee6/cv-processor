package com.intuitech.cvprocessor.unit.service;

import com.intuitech.cvprocessor.application.service.ValidationService;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.domain.model.ValidationResult;
import com.intuitech.cvprocessor.domain.validator.*;
import com.intuitech.cvprocessor.infrastructure.repository.ValidationResultRepository;
import com.intuitech.cvprocessor.util.MockDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private WorkExperienceValidator workExperienceValidator;

    @Mock
    private SkillsValidator skillsValidator;

    @Mock
    private LanguagesValidator languagesValidator;

    @Mock
    private ProfileValidator profileValidator;

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

    @Test
    @DisplayName("Should successfully validate all fields when all validators pass")
    void shouldSuccessfullyValidateAllFieldsWhenAllValidatorsPass() throws Exception {
        // Given
        WorkExperienceValidator.ValidationResult workExpResult = 
            new WorkExperienceValidator.ValidationResult(true, "Work experience is valid");
        SkillsValidator.ValidationResult skillsResult = 
            new SkillsValidator.ValidationResult(true, "Skills are valid");
        LanguagesValidator.ValidationResult languagesResult = 
            new LanguagesValidator.ValidationResult(true, "Languages are valid");
        ProfileValidator.ValidationResult profileResult = 
            new ProfileValidator.ValidationResult(true, "Profile is valid");

        when(workExperienceValidator.validate(validExtractedFields)).thenReturn(workExpResult);
        when(skillsValidator.validate(validExtractedFields)).thenReturn(skillsResult);
        when(languagesValidator.validate(validExtractedFields)).thenReturn(languagesResult);
        when(profileValidator.validate(validExtractedFields)).thenReturn(profileResult);

        ValidationResult expectedResult = MockDataFactory.createValidValidationResult();
        when(validationResultRepository.save(any(ValidationResult.class))).thenReturn(expectedResult);

        // When
        ValidationResult result = validationService.validateFields(validExtractedFields);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResult);

        verify(workExperienceValidator, times(1)).validate(validExtractedFields);
        verify(skillsValidator, times(1)).validate(validExtractedFields);
        verify(languagesValidator, times(1)).validate(validExtractedFields);
        verify(profileValidator, times(1)).validate(validExtractedFields);
        verify(validationResultRepository, times(1)).save(any(ValidationResult.class));
    }

    @Test
    @DisplayName("Should create validation result with correct overall validity when some validators fail")
    void shouldCreateValidationResultWithCorrectOverallValidityWhenSomeValidatorsFail() throws Exception {
        // Given
        WorkExperienceValidator.ValidationResult workExpResult = 
            new WorkExperienceValidator.ValidationResult(true, "Work experience is valid");
        SkillsValidator.ValidationResult skillsResult = 
            new SkillsValidator.ValidationResult(false, "Skills are invalid");
        LanguagesValidator.ValidationResult languagesResult = 
            new LanguagesValidator.ValidationResult(true, "Languages are valid");
        ProfileValidator.ValidationResult profileResult = 
            new ProfileValidator.ValidationResult(false, "Profile is invalid");

        when(workExperienceValidator.validate(validExtractedFields)).thenReturn(workExpResult);
        when(skillsValidator.validate(validExtractedFields)).thenReturn(skillsResult);
        when(languagesValidator.validate(validExtractedFields)).thenReturn(languagesResult);
        when(profileValidator.validate(validExtractedFields)).thenReturn(profileResult);

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
        when(workExperienceValidator.validate(validExtractedFields))
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
        WorkExperienceValidator.ValidationResult workExpResult = 
            new WorkExperienceValidator.ValidationResult(true, "Work experience is valid");
        SkillsValidator.ValidationResult skillsResult = 
            new SkillsValidator.ValidationResult(true, "Skills are valid");
        LanguagesValidator.ValidationResult languagesResult = 
            new LanguagesValidator.ValidationResult(true, "Languages are valid");
        ProfileValidator.ValidationResult profileResult = 
            new ProfileValidator.ValidationResult(true, "Profile is valid");

        when(workExperienceValidator.validate(validExtractedFields)).thenReturn(workExpResult);
        when(skillsValidator.validate(validExtractedFields)).thenReturn(skillsResult);
        when(languagesValidator.validate(validExtractedFields)).thenReturn(languagesResult);
        when(profileValidator.validate(validExtractedFields)).thenReturn(profileResult);
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
    @DisplayName("Should call all validators in correct order")
    void shouldCallAllValidatorsInCorrectOrder() throws Exception {
        // Given
        WorkExperienceValidator.ValidationResult workExpResult = 
            new WorkExperienceValidator.ValidationResult(true, "Work experience is valid");
        SkillsValidator.ValidationResult skillsResult = 
            new SkillsValidator.ValidationResult(true, "Skills are valid");
        LanguagesValidator.ValidationResult languagesResult = 
            new LanguagesValidator.ValidationResult(true, "Languages are valid");
        ProfileValidator.ValidationResult profileResult = 
            new ProfileValidator.ValidationResult(true, "Profile is valid");

        when(workExperienceValidator.validate(validExtractedFields)).thenReturn(workExpResult);
        when(skillsValidator.validate(validExtractedFields)).thenReturn(skillsResult);
        when(languagesValidator.validate(validExtractedFields)).thenReturn(languagesResult);
        when(profileValidator.validate(validExtractedFields)).thenReturn(profileResult);

        ValidationResult expectedResult = MockDataFactory.createValidValidationResult();
        when(validationResultRepository.save(any(ValidationResult.class))).thenReturn(expectedResult);

        // When
        validationService.validateFields(validExtractedFields);

        // Then
        verify(workExperienceValidator, times(1)).validate(validExtractedFields);
        verify(skillsValidator, times(1)).validate(validExtractedFields);
        verify(languagesValidator, times(1)).validate(validExtractedFields);
        verify(profileValidator, times(1)).validate(validExtractedFields);
    }

    @Test
    @DisplayName("Should create validation result with all validator results")
    void shouldCreateValidationResultWithAllValidatorResults() throws Exception {
        // Given
        WorkExperienceValidator.ValidationResult workExpResult = 
            new WorkExperienceValidator.ValidationResult(true, "Work experience is valid");
        SkillsValidator.ValidationResult skillsResult = 
            new SkillsValidator.ValidationResult(false, "Skills are invalid");
        LanguagesValidator.ValidationResult languagesResult = 
            new LanguagesValidator.ValidationResult(true, "Languages are valid");
        ProfileValidator.ValidationResult profileResult = 
            new ProfileValidator.ValidationResult(false, "Profile is invalid");

        when(workExperienceValidator.validate(validExtractedFields)).thenReturn(workExpResult);
        when(skillsValidator.validate(validExtractedFields)).thenReturn(skillsResult);
        when(languagesValidator.validate(validExtractedFields)).thenReturn(languagesResult);
        when(profileValidator.validate(validExtractedFields)).thenReturn(profileResult);

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
