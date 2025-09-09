package com.intuitech.cvprocessor.unit.repository;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.domain.model.ValidationResult;
import com.intuitech.cvprocessor.infrastructure.repository.ValidationResultRepository;
import com.intuitech.cvprocessor.util.MockDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ValidationResultRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ValidationResultRepository Unit Tests")
class ValidationResultRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ValidationResultRepository repository;

    private CVProcessingRequest testRequest;
    private ExtractedFields testExtractedFields;
    private ValidationResult testValidationResult;

    @BeforeEach
    void setUp() {
        // Create and persist CV processing request
        testRequest = MockDataFactory.createPendingRequest();
        testRequest.setFileName("test-cv.pdf");
        entityManager.persistAndFlush(testRequest);

        // Create and persist extracted fields
        testExtractedFields = MockDataFactory.createValidExtractedFields();
        testExtractedFields.setCvProcessingRequest(testRequest);
        entityManager.persistAndFlush(testExtractedFields);

        // Create and persist validation result
        testValidationResult = MockDataFactory.createValidValidationResult();
        testValidationResult.setExtractedFields(testExtractedFields);
        entityManager.persistAndFlush(testValidationResult);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find validation result by extracted fields ID")
    void shouldFindValidationResultByExtractedFieldsId() {
        // When
        Optional<ValidationResult> foundResult = repository.findByExtractedFieldsId(testExtractedFields.getId());

        // Then
        assertThat(foundResult).isPresent();
        assertThat(foundResult.get().getValid()).isTrue();
        assertThat(foundResult.get().getWorkExperienceMessage()).isEqualTo("Work experience validation successful");
        assertThat(foundResult.get().getExtractedFields().getId()).isEqualTo(testExtractedFields.getId());
    }

    @Test
    @DisplayName("Should return empty when extracted fields ID not found")
    void shouldReturnEmptyWhenExtractedFieldsIdNotFound() {
        // When
        Optional<ValidationResult> foundResult = repository.findByExtractedFieldsId(999L);

        // Then
        assertThat(foundResult).isEmpty();
    }

    @Test
    @DisplayName("Should save and retrieve validation result")
    void shouldSaveAndRetrieveValidationResult() {
        // Given
        CVProcessingRequest newRequest = MockDataFactory.createCompletedRequest();
        newRequest.setFileName("new-cv.pdf");
        entityManager.persistAndFlush(newRequest);

        ExtractedFields newFields = MockDataFactory.createValidExtractedFields();
        newFields.setCvProcessingRequest(newRequest);
        entityManager.persistAndFlush(newFields);

        ValidationResult newResult = MockDataFactory.createValidValidationResult();
        newResult.setExtractedFields(newFields);
        newResult.setValid(false);
        newResult.setWorkExperienceMessage("Validation failed");

        // When
        ValidationResult savedResult = repository.save(newResult);
        entityManager.flush();
        entityManager.clear();

        Optional<ValidationResult> retrievedResult = repository.findById(savedResult.getId());

        // Then
        assertThat(retrievedResult).isPresent();
        assertThat(retrievedResult.get().getValid()).isFalse();
        assertThat(retrievedResult.get().getWorkExperienceMessage()).isEqualTo("Validation failed");
        assertThat(retrievedResult.get().getExtractedFields().getId()).isEqualTo(newFields.getId());
    }

    @Test
    @DisplayName("Should update validation result")
    void shouldUpdateValidationResult() {
        // Given
        ValidationResult result = repository.findByExtractedFieldsId(testExtractedFields.getId()).orElse(null);
        assertThat(result).isNotNull();

        // When
        result.setValid(false);
        result.setWorkExperienceMessage("Updated validation message");
        repository.save(result);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ValidationResult> updatedResult = repository.findByExtractedFieldsId(testExtractedFields.getId());
        assertThat(updatedResult).isPresent();
        assertThat(updatedResult.get().getValid()).isFalse();
        assertThat(updatedResult.get().getWorkExperienceMessage()).isEqualTo("Updated validation message");
    }

    @Test
    @DisplayName("Should delete validation result")
    void shouldDeleteValidationResult() {
        // Given
        ValidationResult result = repository.findByExtractedFieldsId(testExtractedFields.getId()).orElse(null);
        assertThat(result).isNotNull();
        Long resultId = result.getId();

        // When
        repository.delete(result);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ValidationResult> deletedResult = repository.findById(resultId);
        assertThat(deletedResult).isEmpty();
    }

    @Test
    @DisplayName("Should find all validation results")
    void shouldFindAllValidationResults() {
        // Given - create another validation result
        CVProcessingRequest anotherRequest = MockDataFactory.createCompletedRequest();
        anotherRequest.setFileName("another-cv.pdf");
        entityManager.persistAndFlush(anotherRequest);

        ExtractedFields anotherFields = MockDataFactory.createValidExtractedFields();
        anotherFields.setCvProcessingRequest(anotherRequest);
        entityManager.persistAndFlush(anotherFields);

        ValidationResult anotherResult = MockDataFactory.createInvalidValidationResult();
        anotherResult.setExtractedFields(anotherFields);
        entityManager.persistAndFlush(anotherResult);
        entityManager.clear();

        // When
        List<ValidationResult> allResults = repository.findAll();

        // Then
        assertThat(allResults).hasSize(2);
        assertThat(allResults).extracting(ValidationResult::getValid)
                .containsExactlyInAnyOrder(true, false);
    }

    @Test
    @DisplayName("Should persist validation result with all fields")
    void shouldPersistValidationResultWithAllFields() {
        // Given
        CVProcessingRequest request = MockDataFactory.createCompletedRequest();
        request.setFileName("full-cv.pdf");
        entityManager.persistAndFlush(request);

        ExtractedFields fields = MockDataFactory.createValidExtractedFields();
        fields.setCvProcessingRequest(request);
        entityManager.persistAndFlush(fields);

        ValidationResult result = MockDataFactory.createValidValidationResult();
        result.setExtractedFields(fields);
        result.setValid(true);
        result.setWorkExperienceMessage("All validations passed");
        result.setErrors(List.of());
        result.setWarnings(List.of("Minor formatting issue"));

        // When
        ValidationResult savedResult = repository.save(result);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ValidationResult> retrievedResult = repository.findById(savedResult.getId());
        assertThat(retrievedResult).isPresent();
        ValidationResult retrieved = retrievedResult.get();
        
        assertThat(retrieved.getValid()).isTrue();
        assertThat(retrieved.getWorkExperienceMessage()).isEqualTo("All validations passed");
        assertThat(retrieved.getErrors()).isEmpty();
        assertThat(retrieved.getWarnings()).containsExactly("Minor formatting issue");
        assertThat(retrieved.getExtractedFields().getId()).isEqualTo(fields.getId());
    }

    @Test
    @DisplayName("Should handle validation result with errors and warnings")
    void shouldHandleValidationResultWithErrorsAndWarnings() {
        // Given
        CVProcessingRequest request = MockDataFactory.createCompletedRequest();
        request.setFileName("error-cv.pdf");
        entityManager.persistAndFlush(request);

        ExtractedFields fields = MockDataFactory.createInvalidExtractedFields();
        fields.setCvProcessingRequest(request);
        entityManager.persistAndFlush(fields);

        ValidationResult result = MockDataFactory.createInvalidValidationResult();
        result.setExtractedFields(fields);
        result.setValid(false);
        result.setWorkExperienceMessage("Validation failed");
        result.setErrors(List.of("Full name is required", "Email format is invalid"));
        result.setWarnings(List.of("Phone number format could be improved"));

        // When
        ValidationResult savedResult = repository.save(result);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ValidationResult> retrievedResult = repository.findById(savedResult.getId());
        assertThat(retrievedResult).isPresent();
        ValidationResult retrieved = retrievedResult.get();
        
        assertThat(retrieved.getValid()).isFalse();
        assertThat(retrieved.getWorkExperienceMessage()).isEqualTo("Validation failed");
        assertThat(retrieved.getErrors()).containsExactly("Full name is required", "Email format is invalid");
        assertThat(retrieved.getWarnings()).containsExactly("Phone number format could be improved");
    }

    @Test
    @DisplayName("Should maintain relationship with extracted fields")
    void shouldMaintainRelationshipWithExtractedFields() {
        // When
        Optional<ValidationResult> foundResult = repository.findByExtractedFieldsId(testExtractedFields.getId());

        // Then
        assertThat(foundResult).isPresent();
        assertThat(foundResult.get().getExtractedFields()).isNotNull();
        assertThat(foundResult.get().getExtractedFields().getId()).isEqualTo(testExtractedFields.getId());
        assertThat(foundResult.get().getExtractedFields().getProfile()).contains("software engineer");
    }

    @Test
    @DisplayName("Should handle null values in validation result")
    void shouldHandleNullValuesInValidationResult() {
        // Given
        CVProcessingRequest request = MockDataFactory.createCompletedRequest();
        request.setFileName("null-cv.pdf");
        entityManager.persistAndFlush(request);

        ExtractedFields fields = MockDataFactory.createValidExtractedFields();
        fields.setCvProcessingRequest(request);
        entityManager.persistAndFlush(fields);

        ValidationResult result = new ValidationResult();
        result.setExtractedFields(fields);
        result.setValid(false);
        result.setWorkExperienceMessage(null);
        result.setErrors(null);
        result.setWarnings(null);

        // When
        ValidationResult savedResult = repository.save(result);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ValidationResult> retrievedResult = repository.findById(savedResult.getId());
        assertThat(retrievedResult).isPresent();
        ValidationResult retrieved = retrievedResult.get();
        
        assertThat(retrieved.getValid()).isFalse();
        assertThat(retrieved.getWorkExperienceMessage()).isNull();
        assertThat(retrieved.getErrors()).isEmpty();
        assertThat(retrieved.getWarnings()).isEmpty();
    }
}
