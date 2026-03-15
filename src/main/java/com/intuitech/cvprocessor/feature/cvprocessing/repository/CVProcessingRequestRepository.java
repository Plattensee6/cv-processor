package com.intuitech.cvprocessor.feature.cvprocessing.repository;

import com.intuitech.cvprocessor.domain.model.CVProcessingRequest;
import com.intuitech.cvprocessor.domain.model.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVProcessingRequestRepository extends JpaRepository<CVProcessingRequest, Long> {

    List<CVProcessingRequest> findByStatus(ProcessingStatus status);

    CVProcessingRequest findByFileName(String fileName);
}

