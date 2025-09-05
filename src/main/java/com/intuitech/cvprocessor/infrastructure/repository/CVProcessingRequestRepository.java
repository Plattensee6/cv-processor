package com.intuitech.cvprocessor.infrastructure.repository;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for CVProcessingRequest entity
 * 
 * Provides data access methods for CV processing requests.
 */
@Repository
public interface CVProcessingRequestRepository extends JpaRepository<CVProcessingRequest, Long> {

    /**
     * Find all processing requests by status
     * 
     * @param status the processing status
     * @return list of requests with the specified status
     */
    List<CVProcessingRequest> findByStatus(CVProcessingRequest.ProcessingStatus status);

    /**
     * Find processing request by file name
     * 
     * @param fileName the file name
     * @return the processing request if found
     */
    CVProcessingRequest findByFileName(String fileName);
}
