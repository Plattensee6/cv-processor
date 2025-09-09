package com.intuitech.cvprocessor.util;

import com.intuitech.cvprocessor.domain.model.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Builder class for creating test data entities
 * 
 * Provides fluent API for creating test objects with sensible defaults.
 */
public class TestDataBuilder {

    public static class CVProcessingRequestBuilder {
        private Long id;
        private String fileName = "test-cv.pdf";
        private String contentType = "application/pdf";
        private Long fileSize = 1024L;
        private String originalContent = "Test CV content";
        private String parsedText = "John Doe\nSoftware Engineer\n5 years experience";

        private CVProcessingRequest.ProcessingStatus status = CVProcessingRequest.ProcessingStatus.UPLOADED;
        private String errorMessage;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public CVProcessingRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CVProcessingRequestBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public CVProcessingRequestBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public CVProcessingRequestBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public CVProcessingRequestBuilder originalContent(String originalContent) {
            this.originalContent = originalContent;
            return this;
        }

        public CVProcessingRequestBuilder parsedText(String parsedText) {
            this.parsedText = parsedText;
            return this;
        }

        public CVProcessingRequestBuilder status(CVProcessingRequest.ProcessingStatus status) {
            this.status = status;
            return this;
        }

        public CVProcessingRequestBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public CVProcessingRequestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CVProcessingRequestBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CVProcessingRequest build() {
            CVProcessingRequest request = new CVProcessingRequest();
            request.setId(id);
            request.setFileName(fileName);
            request.setContentType(contentType);
            request.setFileSize(fileSize);
            request.setOriginalContent(originalContent);
            request.setParsedText(parsedText);
            request.setStatus(status);
            request.setErrorMessage(errorMessage);
            request.setCreatedAt(createdAt);
            request.setUpdatedAt(updatedAt);
            return request;
        }
    }

    public static class ExtractedFieldsBuilder {
        private Long id;
        private CVProcessingRequest processingRequest;
        private Integer workExperienceYears = 2;
        private String workExperienceDetails = "Software Engineer at Test Company (2020-2023)";
        private String skills = "Java, Spring Boot, PostgreSQL";
        private String languages = "English, Spanish";
        private String profile = "Experienced software engineer with 2 years of experience";
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public ExtractedFieldsBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExtractedFieldsBuilder processingRequest(CVProcessingRequest processingRequest) {
            this.processingRequest = processingRequest;
            return this;
        }

        public ExtractedFieldsBuilder workExperienceYears(Integer workExperienceYears) {
            this.workExperienceYears = workExperienceYears;
            return this;
        }

        public ExtractedFieldsBuilder workExperienceDetails(String workExperienceDetails) {
            this.workExperienceDetails = workExperienceDetails;
            return this;
        }

        public ExtractedFieldsBuilder skills(String skills) {
            this.skills = skills;
            return this;
        }

        public ExtractedFieldsBuilder languages(String languages) {
            this.languages = languages;
            return this;
        }

        public ExtractedFieldsBuilder profile(String profile) {
            this.profile = profile;
            return this;
        }

        public ExtractedFieldsBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExtractedFieldsBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ExtractedFields build() {
            ExtractedFields fields = new ExtractedFields();
            fields.setId(id);
            fields.setCvProcessingRequest(processingRequest);
            fields.setWorkExperienceYears(workExperienceYears);
            fields.setWorkExperienceDetails(workExperienceDetails);
            fields.setSkills(skills);
            fields.setLanguages(languages);
            fields.setProfile(profile);
            fields.setCreatedAt(createdAt);
            fields.setUpdatedAt(updatedAt);
            return fields;
        }
    }

    public static class ValidationResultBuilder {
        private Long id;
        private ExtractedFields extractedFields;
        private Boolean workExperienceValid = true;
        private String workExperienceMessage = "Work experience validation successful";
        private Boolean skillsValid = true;
        private String skillsMessage = "Skills validation successful";
        private Boolean languagesValid = true;
        private String languagesMessage = "Languages validation successful";
        private Boolean profileValid = true;
        private String profileMessage = "Profile validation successful";
        private Boolean overallValid = true;
        private List<String> errors = List.of();
        private List<String> warnings = List.of();
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public ValidationResultBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ValidationResultBuilder extractedFields(ExtractedFields extractedFields) {
            this.extractedFields = extractedFields;
            return this;
        }

        public ValidationResultBuilder workExperienceValid(Boolean workExperienceValid) {
            this.workExperienceValid = workExperienceValid;
            return this;
        }

        public ValidationResultBuilder workExperienceMessage(String workExperienceMessage) {
            this.workExperienceMessage = workExperienceMessage;
            return this;
        }

        public ValidationResultBuilder skillsValid(Boolean skillsValid) {
            this.skillsValid = skillsValid;
            return this;
        }

        public ValidationResultBuilder skillsMessage(String skillsMessage) {
            this.skillsMessage = skillsMessage;
            return this;
        }

        public ValidationResultBuilder languagesValid(Boolean languagesValid) {
            this.languagesValid = languagesValid;
            return this;
        }

        public ValidationResultBuilder languagesMessage(String languagesMessage) {
            this.languagesMessage = languagesMessage;
            return this;
        }

        public ValidationResultBuilder profileValid(Boolean profileValid) {
            this.profileValid = profileValid;
            return this;
        }

        public ValidationResultBuilder profileMessage(String profileMessage) {
            this.profileMessage = profileMessage;
            return this;
        }

        public ValidationResultBuilder overallValid(Boolean overallValid) {
            this.overallValid = overallValid;
            return this;
        }

        public ValidationResultBuilder errors(List<String> errors) {
            this.errors = errors;
            return this;
        }

        public ValidationResultBuilder warnings(List<String> warnings) {
            this.warnings = warnings;
            return this;
        }

        public ValidationResultBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ValidationResultBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ValidationResult build() {
            ValidationResult result = new ValidationResult();
            result.setId(id);
            result.setExtractedFields(extractedFields);
            result.setWorkExperienceValid(workExperienceValid);
            result.setWorkExperienceMessage(workExperienceMessage);
            result.setSkillsValid(skillsValid);
            result.setSkillsMessage(skillsMessage);
            result.setLanguagesValid(languagesValid);
            result.setLanguagesMessage(languagesMessage);
            result.setProfileValid(profileValid);
            result.setProfileMessage(profileMessage);
            result.setOverallValid(overallValid);
            result.setErrors(errors);
            result.setWarnings(warnings);
            result.setCreatedAt(createdAt);
            result.setUpdatedAt(updatedAt);
            return result;
        }
    }


    // Static factory methods
    public static CVProcessingRequestBuilder cvProcessingRequest() {
        return new CVProcessingRequestBuilder();
    }

    public static ExtractedFieldsBuilder extractedFields() {
        return new ExtractedFieldsBuilder();
    }

    public static ValidationResultBuilder validationResult() {
        return new ValidationResultBuilder();
    }


    // MultipartFile builder
    public static MultipartFile multipartFile(String filename, String contentType, byte[] content) {
        return new MockMultipartFile("file", filename, contentType, content);
    }

    public static MultipartFile pdfFile() {
        return multipartFile("test-cv.pdf", "application/pdf", "Test PDF content".getBytes());
    }

    public static MultipartFile docFile() {
        return multipartFile("test-cv.doc", "application/msword", "Test DOC content".getBytes());
    }
}
