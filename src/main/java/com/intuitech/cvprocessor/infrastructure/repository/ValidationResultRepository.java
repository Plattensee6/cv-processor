package com.intuitech.cvprocessor.infrastructure.repository;

import com.intuitech.cvprocessor.domain.model.ValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ValidationResult entity
 * 
 * Provides data access methods for validation results.
 */
@Repository
public interface ValidationResultRepository extends JpaRepository<ValidationResult, Long> {
    
    /**
     * Find validation result by extracted fields ID
     * 
     * @param extractedFieldsId the extracted fields ID
     * @return validation result if found
     */
    Optional<ValidationResult> findByExtractedFieldsId(Long extractedFieldsId);
}
