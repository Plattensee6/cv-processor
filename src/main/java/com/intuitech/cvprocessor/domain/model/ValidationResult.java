package com.intuitech.cvprocessor.domain.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing validation results for extracted CV fields
 *
 * This entity stores the results of business rule validation
 * for each extracted field from CV documents.
 */
@Entity
@Table(name = "validation_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "extracted_fields_id", nullable = false)
    private ExtractedFields extractedFields;

    @Column(name = "work_experience_valid")
    private Boolean workExperienceValid;

    @Column(name = "work_experience_message", columnDefinition = "TEXT")
    private String workExperienceMessage;

    @Column(name = "skills_valid")
    private Boolean skillsValid;

    @Column(name = "skills_message", columnDefinition = "TEXT")
    private String skillsMessage;

    @Column(name = "languages_valid")
    private Boolean languagesValid;

    @Column(name = "languages_message", columnDefinition = "TEXT")
    private String languagesMessage;

    @Column(name = "profile_valid")
    private Boolean profileValid;

    @Column(name = "profile_message", columnDefinition = "TEXT")
    private String profileMessage;

    @Column(name = "overall_valid")
    private Boolean overallValid;

    @ElementCollection
    @CollectionTable(name = "validation_result_errors", joinColumns = @JoinColumn(name = "validation_result_id"))
    @Column(name = "error")
    private List<String> errors;

    @ElementCollection
    @CollectionTable(name = "validation_result_warnings", joinColumns = @JoinColumn(name = "validation_result_id"))
    @Column(name = "warning")
    private List<String> warnings;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Boolean getValid() {
        return overallValid;
    }

    public void setValid(Boolean valid) {
        this.overallValid = valid;
    }
}
