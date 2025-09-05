package com.intuitech.cvprocessor.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing extracted fields from a CV
 * 
 * This entity stores the fields extracted by the LLM from CV documents.
 * It includes Work Experience, Skills, Languages, and Profile information.
 */
@Entity
@Table(name = "extracted_fields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_processing_request_id", nullable = false)
    private CVProcessingRequest cvProcessingRequest;

    @Column(name = "work_experience_years")
    private Integer workExperienceYears;

    @Lob
    @Column(name = "work_experience_details", columnDefinition = "TEXT")
    private String workExperienceDetails;

    @Lob
    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Lob
    @Column(name = "languages", columnDefinition = "TEXT")
    private String languages;

    @Lob
    @Column(name = "profile", columnDefinition = "TEXT")
    private String profile;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "extractedFields", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ValidationResult validationResult;
}
