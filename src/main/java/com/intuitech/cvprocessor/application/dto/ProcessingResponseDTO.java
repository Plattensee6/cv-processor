package com.intuitech.cvprocessor.application.dto;

import com.intuitech.cvprocessor.domain.model.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResponseDTO {
    
    private Long requestId;
    private String fileName;
    private ProcessingStatus status;
    private String message;
    private LocalDateTime processedAt;
    
    // Extracted Fields
    private ExtractedFieldsDTO extractedFields;
    
    // Validation Results
    private ValidationResultDTO validationResult;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedFieldsDTO {
        private Integer workExperienceYears;
        private String workExperienceDetails;
        private String skills;
        private String languages;
        private String profile;
        private LocalDateTime extractedAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResultDTO {
        private Boolean workExperienceValid;
        private String workExperienceMessage;
        private Boolean skillsValid;
        private String skillsMessage;
        private Boolean languagesValid;
        private String languagesMessage;
        private Boolean profileValid;
        private String profileMessage;
        private Boolean overallValid;
        private LocalDateTime validatedAt;
    }
}
