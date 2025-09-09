package com.intuitech.cvprocessor.unit.repository;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.infrastructure.repository.CVProcessingRequestRepository;
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
 * Unit tests for CVProcessingRequestRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CVProcessingRequestRepository Unit Tests")
class CVProcessingRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CVProcessingRequestRepository repository;

    private CVProcessingRequest pendingRequest;
    private CVProcessingRequest completedRequest;
    private CVProcessingRequest failedRequest;

    @BeforeEach
    void setUp() {
        // Create test data
        pendingRequest = MockDataFactory.createPendingRequest();
        pendingRequest.setFileName("pending-cv.pdf");
        
        completedRequest = MockDataFactory.createCompletedRequest();
        completedRequest.setFileName("completed-cv.pdf");
        
        failedRequest = MockDataFactory.createFailedRequest();
        failedRequest.setFileName("failed-cv.pdf");

        // Save to database
        entityManager.persistAndFlush(pendingRequest);
        entityManager.persistAndFlush(completedRequest);
        entityManager.persistAndFlush(failedRequest);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find all requests by status")
    void shouldFindAllRequestsByStatus() {
        // When
        List<CVProcessingRequest> pendingRequests = repository.findByStatus(CVProcessingRequest.ProcessingStatus.UPLOADED);
        List<CVProcessingRequest> completedRequests = repository.findByStatus(CVProcessingRequest.ProcessingStatus.COMPLETED);
        List<CVProcessingRequest> failedRequests = repository.findByStatus(CVProcessingRequest.ProcessingStatus.FAILED);

        // Then
        assertThat(pendingRequests).hasSize(1);
        assertThat(pendingRequests.get(0).getFileName()).isEqualTo("pending-cv.pdf");
        assertThat(pendingRequests.get(0).getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.UPLOADED);

        assertThat(completedRequests).hasSize(1);
        assertThat(completedRequests.get(0).getFileName()).isEqualTo("completed-cv.pdf");
        assertThat(completedRequests.get(0).getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.COMPLETED);

        assertThat(failedRequests).hasSize(1);
        assertThat(failedRequests.get(0).getFileName()).isEqualTo("failed-cv.pdf");
        assertThat(failedRequests.get(0).getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.FAILED);
    }

    @Test
    @DisplayName("Should find request by file name")
    void shouldFindRequestByFileName() {
        // When
        CVProcessingRequest foundRequest = repository.findByFileName("pending-cv.pdf");

        // Then
        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getFileName()).isEqualTo("pending-cv.pdf");
        assertThat(foundRequest.getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.UPLOADED);
    }

    @Test
    @DisplayName("Should return null when file name not found")
    void shouldReturnNullWhenFileNameNotFound() {
        // When
        CVProcessingRequest foundRequest = repository.findByFileName("non-existent.pdf");

        // Then
        assertThat(foundRequest).isNull();
    }

    @Test
    @DisplayName("Should save and retrieve request")
    void shouldSaveAndRetrieveRequest() {
        // Given
        CVProcessingRequest newRequest = MockDataFactory.createPendingRequest();
        newRequest.setFileName("new-cv.pdf");

        // When
        CVProcessingRequest savedRequest = repository.save(newRequest);
        entityManager.flush();
        entityManager.clear();

        Optional<CVProcessingRequest> retrievedRequest = repository.findById(savedRequest.getId());

        // Then
        assertThat(retrievedRequest).isPresent();
        assertThat(retrievedRequest.get().getFileName()).isEqualTo("new-cv.pdf");
        assertThat(retrievedRequest.get().getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.UPLOADED);
    }

    @Test
    @DisplayName("Should update request status")
    void shouldUpdateRequestStatus() {
        // Given
        CVProcessingRequest request = repository.findByFileName("pending-cv.pdf");
        assertThat(request).isNotNull();

        // When
        request.setStatus(CVProcessingRequest.ProcessingStatus.EXTRACTING);
        repository.save(request);
        entityManager.flush();
        entityManager.clear();

        // Then
        CVProcessingRequest updatedRequest = repository.findByFileName("pending-cv.pdf");
        assertThat(updatedRequest.getStatus()).isEqualTo(CVProcessingRequest.ProcessingStatus.EXTRACTING);
    }

    @Test
    @DisplayName("Should delete request")
    void shouldDeleteRequest() {
        // Given
        CVProcessingRequest request = repository.findByFileName("pending-cv.pdf");
        assertThat(request).isNotNull();
        Long requestId = request.getId();

        // When
        repository.delete(request);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CVProcessingRequest> deletedRequest = repository.findById(requestId);
        assertThat(deletedRequest).isEmpty();
    }

    @Test
    @DisplayName("Should find all requests")
    void shouldFindAllRequests() {
        // When
        List<CVProcessingRequest> allRequests = repository.findAll();

        // Then
        assertThat(allRequests).hasSize(3);
        assertThat(allRequests).extracting(CVProcessingRequest::getFileName)
                .containsExactlyInAnyOrder("pending-cv.pdf", "completed-cv.pdf", "failed-cv.pdf");
    }

    @Test
    @DisplayName("Should count requests by status")
    void shouldCountRequestsByStatus() {
        // When
        long pendingCount = repository.findByStatus(CVProcessingRequest.ProcessingStatus.UPLOADED).size();
        long completedCount = repository.findByStatus(CVProcessingRequest.ProcessingStatus.COMPLETED).size();
        long failedCount = repository.findByStatus(CVProcessingRequest.ProcessingStatus.FAILED).size();

        // Then
        assertThat(pendingCount).isEqualTo(1);
        assertThat(completedCount).isEqualTo(1);
        assertThat(failedCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle case-sensitive file name search")
    void shouldHandleCaseSensitiveFileNameSearch() {
        // When
        CVProcessingRequest foundRequest = repository.findByFileName("PENDING-CV.PDF");

        // Then
        assertThat(foundRequest).isNull(); // Should be case-sensitive
    }

    @Test
    @DisplayName("Should persist request with all fields")
    void shouldPersistRequestWithAllFields() {
        // Given
        CVProcessingRequest request = MockDataFactory.createPendingRequest();
        request.setFileName("full-cv.pdf");
        request.setContentType("application/pdf");
        request.setFileSize(2048L);
        request.setOriginalContent("Original file content");
        request.setParsedText("Parsed text content");
        request.setErrorMessage("Some error message");

        // When
        repository.save(request);
        entityManager.flush();
        entityManager.clear();

        // Then
        CVProcessingRequest retrievedRequest = repository.findByFileName("full-cv.pdf");
        assertThat(retrievedRequest).isNotNull();
        assertThat(retrievedRequest.getFileName()).isEqualTo("full-cv.pdf");
        assertThat(retrievedRequest.getContentType()).isEqualTo("application/pdf");
        assertThat(retrievedRequest.getFileSize()).isEqualTo(2048L);
        assertThat(retrievedRequest.getOriginalContent()).isEqualTo("Original file content");
        assertThat(retrievedRequest.getParsedText()).isEqualTo("Parsed text content");
        assertThat(retrievedRequest.getErrorMessage()).isEqualTo("Some error message");
    }
}
