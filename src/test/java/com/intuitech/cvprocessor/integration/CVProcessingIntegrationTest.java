package com.intuitech.cvprocessor.integration;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ProcessingStatus;
import com.intuitech.cvprocessor.feature.cvprocessing.repository.CVProcessingRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CV processing entities
 */
@DataJpaTest
@ActiveProfiles("test")
class CVProcessingIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CVProcessingRequestRepository cvProcessingRequestRepository;

    @Test
    void saveAndRetrieveCVProcessingRequest() {
        // Given
        CVProcessingRequest request = CVProcessingRequest.builder()
                .fileName("test.pdf")
                .contentType("application/pdf")
                .fileSize(1024L)
                .originalContent("test content")
                .parsedText("parsed text")
                .status(ProcessingStatus.UPLOADED)
                .build();

        // When
        CVProcessingRequest saved = cvProcessingRequestRepository.save(request);
        entityManager.flush();
        entityManager.clear();

        // Then
        CVProcessingRequest found = cvProcessingRequestRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("test.pdf", found.getFileName());
        assertEquals(ProcessingStatus.UPLOADED, found.getStatus());
    }

    @Test
    void findByStatus() {
        // Given
        CVProcessingRequest request1 = CVProcessingRequest.builder()
                .fileName("test1.pdf")
                .contentType("application/pdf")
                .fileSize(1024L)
                .status(ProcessingStatus.UPLOADED)
                .build();

        CVProcessingRequest request2 = CVProcessingRequest.builder()
                .fileName("test2.pdf")
                .contentType("application/pdf")
                .fileSize(2048L)
                .status(ProcessingStatus.COMPLETED)
                .build();

        cvProcessingRequestRepository.save(request1);
        cvProcessingRequestRepository.save(request2);
        entityManager.flush();

        // When
        var uploadedRequests = cvProcessingRequestRepository.findByStatus(ProcessingStatus.UPLOADED);

        // Then
        assertEquals(1, uploadedRequests.size());
        assertEquals("test1.pdf", uploadedRequests.get(0).getFileName());
    }
}
