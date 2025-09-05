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
 * Entity representing a CV processing request
 * 
 * This entity stores metadata about uploaded CV files and their processing status.
 * It serves as the foundation for all CV processing operations.
 */
@Entity
@Table(name = "cv_processing_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVProcessingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String originalContent;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String parsedText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "cvProcessingRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ExtractedFields extractedFields;

    /**
     * Enum representing the processing status of a CV request
     */
    public enum ProcessingStatus {
        UPLOADED,
        PARSING,
        EXTRACTING,
        VALIDATING,
        COMPLETED,
        FAILED
    }
}
