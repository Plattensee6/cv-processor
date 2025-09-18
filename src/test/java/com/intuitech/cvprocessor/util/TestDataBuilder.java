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

        private CVProcessingRequest.ProcessingStatus status = CVProcessingRequest.ProcessingStatus.PENDING;
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
        private String fullName = "John Doe";
        private String email = "john.doe@example.com";
        private String phone = "+1234567890";
        private String address = "123 Main St, City, Country";
        private String summary = "Experienced software engineer";
        private List<String> skills = List.of("Java", "Spring Boot", "PostgreSQL");
        private List<String> languages = List.of("English", "Spanish");
        private List<WorkExperience> workExperience = List.of();
        private List<Education> education = List.of();
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

        public ExtractedFieldsBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public ExtractedFieldsBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ExtractedFieldsBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public ExtractedFieldsBuilder address(String address) {
            this.address = address;
            return this;
        }

        public ExtractedFieldsBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public ExtractedFieldsBuilder skills(List<String> skills) {
            this.skills = skills;
            return this;
        }

        public ExtractedFieldsBuilder languages(List<String> languages) {
            this.languages = languages;
            return this;
        }

        public ExtractedFieldsBuilder workExperience(List<WorkExperience> workExperience) {
            this.workExperience = workExperience;
            return this;
        }

        public ExtractedFieldsBuilder education(List<Education> education) {
            this.education = education;
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
            fields.setProcessingRequest(processingRequest);
            fields.setFullName(fullName);
            fields.setEmail(email);
            fields.setPhone(phone);
            fields.setAddress(address);
            fields.setSummary(summary);
            fields.setSkills(skills);
            fields.setLanguages(languages);
            fields.setWorkExperience(workExperience);
            fields.setEducation(education);
            fields.setCreatedAt(createdAt);
            fields.setUpdatedAt(updatedAt);
            return fields;
        }
    }

    public static class ValidationResultBuilder {
        private Long id;
        private ExtractedFields extractedFields;
        private boolean isValid = true;
        private String validationMessage = "Validation successful";
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

        public ValidationResultBuilder isValid(boolean isValid) {
            this.isValid = isValid;
            return this;
        }

        public ValidationResultBuilder validationMessage(String validationMessage) {
            this.validationMessage = validationMessage;
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
            result.setValid(isValid);
            result.setValidationMessage(validationMessage);
            result.setErrors(errors);
            result.setWarnings(warnings);
            result.setCreatedAt(createdAt);
            result.setUpdatedAt(updatedAt);
            return result;
        }
    }

    public static class WorkExperienceBuilder {
        private String company = "Test Company";
        private String position = "Software Engineer";
        private String startDate = "2020-01-01";
        private String endDate = "2023-12-31";
        private String description = "Developed software applications";
        private List<String> technologies = List.of("Java", "Spring Boot");

        public WorkExperienceBuilder company(String company) {
            this.company = company;
            return this;
        }

        public WorkExperienceBuilder position(String position) {
            this.position = position;
            return this;
        }

        public WorkExperienceBuilder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public WorkExperienceBuilder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public WorkExperienceBuilder description(String description) {
            this.description = description;
            return this;
        }

        public WorkExperienceBuilder technologies(List<String> technologies) {
            this.technologies = technologies;
            return this;
        }

        public WorkExperience build() {
            WorkExperience experience = new WorkExperience();
            experience.setCompany(company);
            experience.setPosition(position);
            experience.setStartDate(startDate);
            experience.setEndDate(endDate);
            experience.setDescription(description);
            experience.setTechnologies(technologies);
            return experience;
        }
    }

    public static class EducationBuilder {
        private String institution = "Test University";
        private String degree = "Bachelor of Science";
        private String field = "Computer Science";
        private String startDate = "2016-09-01";
        private String endDate = "2020-06-30";
        private String gpa = "3.8";
        private List<String> achievements = List.of("Dean's List", "Honors");

        public EducationBuilder institution(String institution) {
            this.institution = institution;
            return this;
        }

        public EducationBuilder degree(String degree) {
            this.degree = degree;
            return this;
        }

        public EducationBuilder field(String field) {
            this.field = field;
            return this;
        }

        public EducationBuilder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public EducationBuilder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public EducationBuilder gpa(String gpa) {
            this.gpa = gpa;
            return this;
        }

        public EducationBuilder achievements(List<String> achievements) {
            this.achievements = achievements;
            return this;
        }

        public Education build() {
            Education education = new Education();
            education.setInstitution(institution);
            education.setDegree(degree);
            education.setField(field);
            education.setStartDate(startDate);
            education.setEndDate(endDate);
            education.setGpa(gpa);
            education.setAchievements(achievements);
            return education;
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

    public static WorkExperienceBuilder workExperience() {
        return new WorkExperienceBuilder();
    }

    public static EducationBuilder education() {
        return new EducationBuilder();
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
