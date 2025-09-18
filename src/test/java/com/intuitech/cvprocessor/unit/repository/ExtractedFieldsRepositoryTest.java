package com.intuitech.cvprocessor.unit.repository;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import com.intuitech.cvprocessor.infrastructure.repository.ExtractedFieldsRepository;
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
 * Unit tests for ExtractedFieldsRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ExtractedFieldsRepository Unit Tests")
class ExtractedFieldsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExtractedFieldsRepository repository;

    private CVProcessingRequest testRequest;
    private ExtractedFields testExtractedFields;

    @BeforeEach
    void setUp() {
        // Create and persist CV processing request
        testRequest = MockDataFactory.createPendingRequest();
        testRequest.setFileName("test-cv.pdf");
        entityManager.persistAndFlush(testRequest);

        // Create extracted fields
        testExtractedFields = MockDataFactory.createValidExtractedFields();
        testExtractedFields.setCvProcessingRequest(testRequest);
        entityManager.persistAndFlush(testExtractedFields);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find extracted fields by CV processing request ID")
    void shouldFindExtractedFieldsByCvProcessingRequestId() {
        // When
        Optional<ExtractedFields> foundFields = repository.findByCvProcessingRequestId(testRequest.getId());

        // Then
        assertThat(foundFields).isPresent();
        assertThat(foundFields.get().getFullName()).isEqualTo("John Doe");
        assertThat(foundFields.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundFields.get().getCvProcessingRequest().getId()).isEqualTo(testRequest.getId());
    }

    @Test
    @DisplayName("Should return empty when CV processing request ID not found")
    void shouldReturnEmptyWhenCvProcessingRequestIdNotFound() {
        // When
        Optional<ExtractedFields> foundFields = repository.findByCvProcessingRequestId(999L);

        // Then
        assertThat(foundFields).isEmpty();
    }

    @Test
    @DisplayName("Should save and retrieve extracted fields")
    void shouldSaveAndRetrieveExtractedFields() {
        // Given
        CVProcessingRequest newRequest = MockDataFactory.createCompletedRequest();
        newRequest.setFileName("new-cv.pdf");
        entityManager.persistAndFlush(newRequest);

        ExtractedFields newFields = MockDataFactory.createValidExtractedFields();
        newFields.setCvProcessingRequest(newRequest);
        newFields.setFullName("Jane Smith");
        newFields.setEmail("jane.smith@example.com");

        // When
        ExtractedFields savedFields = repository.save(newFields);
        entityManager.flush();
        entityManager.clear();

        Optional<ExtractedFields> retrievedFields = repository.findById(savedFields.getId());

        // Then
        assertThat(retrievedFields).isPresent();
        assertThat(retrievedFields.get().getFullName()).isEqualTo("Jane Smith");
        assertThat(retrievedFields.get().getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(retrievedFields.get().getCvProcessingRequest().getId()).isEqualTo(newRequest.getId());
    }

    @Test
    @DisplayName("Should update extracted fields")
    void shouldUpdateExtractedFields() {
        // Given
        ExtractedFields fields = repository.findByCvProcessingRequestId(testRequest.getId()).orElse(null);
        assertThat(fields).isNotNull();

        // When
        fields.setFullName("Updated Name");
        fields.setEmail("updated@example.com");
        repository.save(fields);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ExtractedFields> updatedFields = repository.findByCvProcessingRequestId(testRequest.getId());
        assertThat(updatedFields).isPresent();
        assertThat(updatedFields.get().getFullName()).isEqualTo("Updated Name");
        assertThat(updatedFields.get().getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Should delete extracted fields")
    void shouldDeleteExtractedFields() {
        // Given
        ExtractedFields fields = repository.findByCvProcessingRequestId(testRequest.getId()).orElse(null);
        assertThat(fields).isNotNull();
        Long fieldsId = fields.getId();

        // When
        repository.delete(fields);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ExtractedFields> deletedFields = repository.findById(fieldsId);
        assertThat(deletedFields).isEmpty();
    }

    @Test
    @DisplayName("Should find all extracted fields")
    void shouldFindAllExtractedFields() {
        // Given - create another set of extracted fields
        CVProcessingRequest anotherRequest = MockDataFactory.createCompletedRequest();
        anotherRequest.setFileName("another-cv.pdf");
        entityManager.persistAndFlush(anotherRequest);

        ExtractedFields anotherFields = MockDataFactory.createValidExtractedFields();
        anotherFields.setCvProcessingRequest(anotherRequest);
        anotherFields.setFullName("Bob Wilson");
        entityManager.persistAndFlush(anotherFields);
        entityManager.clear();

        // When
        List<ExtractedFields> allFields = repository.findAll();

        // Then
        assertThat(allFields).hasSize(2);
        assertThat(allFields).extracting(ExtractedFields::getFullName)
                .containsExactlyInAnyOrder("John Doe", "Bob Wilson");
    }

    @Test
    @DisplayName("Should persist extracted fields with all fields")
    void shouldPersistExtractedFieldsWithAllFields() {
        // Given
        CVProcessingRequest request = MockDataFactory.createCompletedRequest();
        request.setFileName("full-cv.pdf");
        entityManager.persistAndFlush(request);

        ExtractedFields fields = MockDataFactory.createValidExtractedFields();
        fields.setCvProcessingRequest(request);
        fields.setFullName("Full Name");
        fields.setEmail("full@example.com");
        fields.setPhone("+1234567890");
        fields.setAddress("123 Main St, City, Country");
        fields.setSummary("Experienced professional");
        fields.setSkills(List.of("Java", "Spring", "PostgreSQL"));
        fields.setLanguages(List.of("English", "Spanish"));

        // When
        ExtractedFields savedFields = repository.save(fields);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ExtractedFields> retrievedFields = repository.findById(savedFields.getId());
        assertThat(retrievedFields).isPresent();
        ExtractedFields retrieved = retrievedFields.get();
        
        assertThat(retrieved.getFullName()).isEqualTo("Full Name");
        assertThat(retrieved.getEmail()).isEqualTo("full@example.com");
        assertThat(retrieved.getPhone()).isEqualTo("+1234567890");
        assertThat(retrieved.getAddress()).isEqualTo("123 Main St, City, Country");
        assertThat(retrieved.getSummary()).isEqualTo("Experienced professional");
        assertThat(retrieved.getSkills()).containsExactly("Java", "Spring", "PostgreSQL");
        assertThat(retrieved.getLanguages()).containsExactly("English", "Spanish");
        assertThat(retrieved.getCvProcessingRequest().getId()).isEqualTo(request.getId());
    }

    @Test
    @DisplayName("Should handle null values in extracted fields")
    void shouldHandleNullValuesInExtractedFields() {
        // Given
        CVProcessingRequest request = MockDataFactory.createCompletedRequest();
        request.setFileName("null-cv.pdf");
        entityManager.persistAndFlush(request);

        ExtractedFields fields = new ExtractedFields();
        fields.setCvProcessingRequest(request);
        fields.setFullName(null);
        fields.setEmail(null);
        fields.setPhone(null);
        fields.setAddress(null);
        fields.setSummary(null);
        fields.setSkills(null);
        fields.setLanguages(null);

        // When
        ExtractedFields savedFields = repository.save(fields);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ExtractedFields> retrievedFields = repository.findById(savedFields.getId());
        assertThat(retrievedFields).isPresent();
        ExtractedFields retrieved = retrievedFields.get();
        
        assertThat(retrieved.getFullName()).isNull();
        assertThat(retrieved.getEmail()).isNull();
        assertThat(retrieved.getPhone()).isNull();
        assertThat(retrieved.getAddress()).isNull();
        assertThat(retrieved.getSummary()).isNull();
        assertThat(retrieved.getSkills()).isNull();
        assertThat(retrieved.getLanguages()).isNull();
    }

    @Test
    @DisplayName("Should maintain relationship with CV processing request")
    void shouldMaintainRelationshipWithCvProcessingRequest() {
        // When
        Optional<ExtractedFields> foundFields = repository.findByCvProcessingRequestId(testRequest.getId());

        // Then
        assertThat(foundFields).isPresent();
        assertThat(foundFields.get().getCvProcessingRequest()).isNotNull();
        assertThat(foundFields.get().getCvProcessingRequest().getId()).isEqualTo(testRequest.getId());
        assertThat(foundFields.get().getCvProcessingRequest().getFileName()).isEqualTo("test-cv.pdf");
    }
}
