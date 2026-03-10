package com.intuitech.cvprocessor.feature.cvprocessing.repository;

import com.intuitech.cvprocessor.domain.model.ExtractedFields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ExtractedFields entity
 *
 * Provides data access methods for extracted CV fields.
 */
@Repository
public interface ExtractedFieldsRepository extends JpaRepository<ExtractedFields, Long> {

    /**
     * Find extracted fields by CV processing request ID
     *
     * @param cvProcessingRequestId the CV processing request ID
     * @return extracted fields if found
     */
    Optional<ExtractedFields> findByCvProcessingRequestId(Long cvProcessingRequestId);
}

