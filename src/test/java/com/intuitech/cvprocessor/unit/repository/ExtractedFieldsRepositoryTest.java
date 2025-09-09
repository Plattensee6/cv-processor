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
        assertThat(foundFields.get().getProfile()).isEqualTo("Experienced software engineer with 5+ years in Java development. Bachelor of Science in Computer Science from University of Technology (2016-2020) with GPA 3.8");
        assertThat(foundFields.get().getSkills()).isEqualTo("Java, Spring Boot, PostgreSQL, Docker, Kubernetes");
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
        newFields.setProfile("Jane Smith - Senior Developer");
        newFields.setSkills("Python, Django, React");

        // When
        ExtractedFields savedFields = repository.save(newFields);
        entityManager.flush();
        entityManager.clear();

        Optional<ExtractedFields> retrievedFields = repository.findById(savedFields.getId());

        // Then
        assertThat(retrievedFields).isPresent();
        assertThat(retrievedFields.get().getProfile()).isEqualTo("Jane Smith - Senior Developer");
        assertThat(retrievedFields.get().getSkills()).isEqualTo("Python, Django, React");
        assertThat(retrievedFields.get().getCvProcessingRequest().getId()).isEqualTo(newRequest.getId());
    }

    @Test
    @DisplayName("Should update extracted fields")
    void shouldUpdateExtractedFields() {
        // Given
        ExtractedFields fields = repository.findByCvProcessingRequestId(testRequest.getId()).orElse(null);
        assertThat(fields).isNotNull();

        // When
        fields.setProfile("Updated Profile");
        fields.setSkills("Updated Skills");
        repository.save(fields);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ExtractedFields> updatedFields = repository.findByCvProcessingRequestId(testRequest.getId());
        assertThat(updatedFields).isPresent();
        assertThat(updatedFields.get().getProfile()).isEqualTo("Updated Profile");
        assertThat(updatedFields.get().getSkills()).isEqualTo("Updated Skills");
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
        anotherFields.setProfile("Bob Wilson - DevOps Engineer");
        entityManager.persistAndFlush(anotherFields);
        entityManager.clear();

        // When
        List<ExtractedFields> allFields = repository.findAll();

        // Then
        assertThat(allFields).hasSize(2);
        assertThat(allFields).extracting(ExtractedFields::getProfile)
                .containsExactlyInAnyOrder("Experienced software engineer with 5+ years in Java development. Bachelor of Science in Computer Science from University of Technology (2016-2020) with GPA 3.8", "Bob Wilson - DevOps Engineer");
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
        fields.setWorkExperienceYears(5);
        fields.setWorkExperienceDetails("Senior Software Engineer at Tech Corp (2019-2024)");
        fields.setProfile("Experienced professional");
        fields.setSkills("Java, Spring, PostgreSQL");
        fields.setLanguages("English, Spanish");

        // When
        ExtractedFields savedFields = repository.save(fields);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<ExtractedFields> retrievedFields = repository.findById(savedFields.getId());
        assertThat(retrievedFields).isPresent();
        ExtractedFields retrieved = retrievedFields.get();
        
        assertThat(retrieved.getWorkExperienceYears()).isEqualTo(5);
        assertThat(retrieved.getWorkExperienceDetails()).isEqualTo("Senior Software Engineer at Tech Corp (2019-2024)");
        assertThat(retrieved.getProfile()).isEqualTo("Experienced professional");
        assertThat(retrieved.getSkills()).isEqualTo("Java, Spring, PostgreSQL");
        assertThat(retrieved.getLanguages()).isEqualTo("English, Spanish");
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
        fields.setWorkExperienceYears(null);
        fields.setWorkExperienceDetails(null);
        fields.setProfile(null);
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
        
        assertThat(retrieved.getWorkExperienceYears()).isNull();
        assertThat(retrieved.getWorkExperienceDetails()).isNull();
        assertThat(retrieved.getProfile()).isNull();
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
