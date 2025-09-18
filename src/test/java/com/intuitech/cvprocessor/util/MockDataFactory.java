package com.intuitech.cvprocessor.util;

import com.intuitech.cvprocessor.domain.model.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Factory class for creating mock data objects
 * 
 * Provides predefined mock objects for testing scenarios.
 */
public class MockDataFactory {

    // CV Processing Request mocks
    public static CVProcessingRequest createPendingRequest() {
        return TestDataBuilder.cvProcessingRequest()
                .fileName("john-doe-cv.pdf")
                .contentType("application/pdf")
                .fileSize(2048L)
                .status(ProcessingStatus.PENDING)
                .build();
    }

    public static CVProcessingRequest createCompletedRequest() {
        return TestDataBuilder.cvProcessingRequest()
                .fileName("jane-smith-cv.pdf")
                .contentType("application/pdf")
                .fileSize(1536L)
                .status(ProcessingStatus.COMPLETED)
                .parsedText("Jane Smith\nSenior Developer\n8 years experience in Java")
                .build();
    }

    public static CVProcessingRequest createFailedRequest() {
        return TestDataBuilder.cvProcessingRequest()
                .fileName("invalid-file.txt")
                .contentType("text/plain")
                .fileSize(512L)
                .status(ProcessingStatus.FAILED)
                .errorMessage("Unsupported file type")
                .build();
    }

    // Extracted Fields mocks
    public static ExtractedFields createValidExtractedFields() {
        return TestDataBuilder.extractedFields()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St, New York, NY 10001")
                .summary("Experienced software engineer with 5+ years in Java development")
                .skills(List.of("Java", "Spring Boot", "PostgreSQL", "Docker", "Kubernetes"))
                .languages(List.of("English", "Spanish"))
                .workExperience(List.of(
                        TestDataBuilder.workExperience()
                                .company("Tech Corp")
                                .position("Senior Software Engineer")
                                .startDate("2020-01-01")
                                .endDate("2023-12-31")
                                .description("Led development of microservices architecture")
                                .technologies(List.of("Java", "Spring Boot", "Docker"))
                                .build()
                ))
                .education(List.of(
                        TestDataBuilder.education()
                                .institution("University of Technology")
                                .degree("Bachelor of Science")
                                .field("Computer Science")
                                .startDate("2016-09-01")
                                .endDate("2020-06-30")
                                .gpa("3.8")
                                .build()
                ))
                .build();
    }

    public static ExtractedFields createIncompleteExtractedFields() {
        return TestDataBuilder.extractedFields()
                .fullName("Jane Smith")
                .email("jane.smith@example.com")
                .phone("") // Missing phone
                .address("") // Missing address
                .summary("Developer with some experience")
                .skills(List.of("Python", "Django"))
                .languages(List.of("English"))
                .workExperience(List.of())
                .education(List.of())
                .build();
    }

    public static ExtractedFields createInvalidExtractedFields() {
        return TestDataBuilder.extractedFields()
                .fullName("") // Invalid: empty name
                .email("invalid-email") // Invalid: malformed email
                .phone("123") // Invalid: too short
                .address("")
                .summary("")
                .skills(List.of())
                .languages(List.of())
                .workExperience(List.of())
                .education(List.of())
                .build();
    }

    // Validation Result mocks
    public static ValidationResult createValidValidationResult() {
        return TestDataBuilder.validationResult()
                .isValid(true)
                .validationMessage("All fields are valid")
                .errors(List.of())
                .warnings(List.of("Phone number format could be improved"))
                .build();
    }

    public static ValidationResult createInvalidValidationResult() {
        return TestDataBuilder.validationResult()
                .isValid(false)
                .validationMessage("Validation failed")
                .errors(List.of(
                        "Full name is required",
                        "Email format is invalid",
                        "At least one skill is required"
                ))
                .warnings(List.of())
                .build();
    }

    // Work Experience mocks
    public static WorkExperience createValidWorkExperience() {
        return TestDataBuilder.workExperience()
                .company("Google")
                .position("Software Engineer")
                .startDate("2021-01-01")
                .endDate("2023-12-31")
                .description("Developed scalable web applications using Java and Spring Boot")
                .technologies(List.of("Java", "Spring Boot", "Kubernetes", "GCP"))
                .build();
    }

    public static WorkExperience createCurrentWorkExperience() {
        return TestDataBuilder.workExperience()
                .company("Microsoft")
                .position("Senior Software Engineer")
                .startDate("2023-01-01")
                .endDate("") // Current position
                .description("Leading development of cloud-native applications")
                .technologies(List.of("C#", ".NET", "Azure", "Docker"))
                .build();
    }

    // Education mocks
    public static Education createValidEducation() {
        return TestDataBuilder.education()
                .institution("Stanford University")
                .degree("Master of Science")
                .field("Computer Science")
                .startDate("2018-09-01")
                .endDate("2020-06-30")
                .gpa("3.9")
                .achievements(List.of("Summa Cum Laude", "Dean's List"))
                .build();
    }

    public static Education createBachelorEducation() {
        return TestDataBuilder.education()
                .institution("MIT")
                .degree("Bachelor of Science")
                .field("Computer Science")
                .startDate("2014-09-01")
                .endDate("2018-06-30")
                .gpa("3.7")
                .achievements(List.of("Magna Cum Laude"))
                .build();
    }

    // MultipartFile mocks
    public static MultipartFile createValidPdfFile() {
        return new MockMultipartFile(
                "file",
                "john-doe-cv.pdf",
                "application/pdf",
                "PDF content with CV data".getBytes()
        );
    }

    public static MultipartFile createValidDocFile() {
        return new MockMultipartFile(
                "file",
                "jane-smith-cv.doc",
                "application/msword",
                "DOC content with CV data".getBytes()
        );
    }

    public static MultipartFile createValidDocxFile() {
        return new MockMultipartFile(
                "file",
                "bob-wilson-cv.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "DOCX content with CV data".getBytes()
        );
    }

    public static MultipartFile createInvalidFile() {
        return new MockMultipartFile(
                "file",
                "invalid-file.txt",
                "text/plain",
                "Plain text content".getBytes()
        );
    }

    public static MultipartFile createLargeFile() {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        return new MockMultipartFile(
                "file",
                "large-cv.pdf",
                "application/pdf",
                largeContent
        );
    }

    public static MultipartFile createEmptyFile() {
        return new MockMultipartFile(
                "file",
                "empty-cv.pdf",
                "application/pdf",
                new byte[0]
        );
    }

    // Complete processing pipeline mocks
    public static class CompleteProcessingPipeline {
        private final CVProcessingRequest request;
        private final ExtractedFields extractedFields;
        private final ValidationResult validationResult;

        public CompleteProcessingPipeline(CVProcessingRequest request, 
                                        ExtractedFields extractedFields, 
                                        ValidationResult validationResult) {
            this.request = request;
            this.extractedFields = extractedFields;
            this.validationResult = validationResult;
        }

        public CVProcessingRequest getRequest() {
            return request;
        }

        public ExtractedFields getExtractedFields() {
            return extractedFields;
        }

        public ValidationResult getValidationResult() {
            return validationResult;
        }
    }

    public static CompleteProcessingPipeline createSuccessfulPipeline() {
        CVProcessingRequest request = createCompletedRequest();
        ExtractedFields fields = createValidExtractedFields();
        ValidationResult result = createValidValidationResult();
        
        fields.setProcessingRequest(request);
        result.setExtractedFields(fields);
        
        return new CompleteProcessingPipeline(request, fields, result);
    }

    public static CompleteProcessingPipeline createFailedPipeline() {
        CVProcessingRequest request = createFailedRequest();
        ExtractedFields fields = createInvalidExtractedFields();
        ValidationResult result = createInvalidValidationResult();
        
        fields.setProcessingRequest(request);
        result.setExtractedFields(fields);
        
        return new CompleteProcessingPipeline(request, fields, result);
    }

    // Test scenarios
    public static class TestScenarios {
        public static final String VALID_CV_TEXT = """
                John Doe
                Software Engineer
                john.doe@example.com
                +1234567890
                123 Main St, New York, NY 10001
                
                Summary:
                Experienced software engineer with 5+ years in Java development.
                
                Skills:
                - Java
                - Spring Boot
                - PostgreSQL
                - Docker
                - Kubernetes
                
                Work Experience:
                Tech Corp - Senior Software Engineer (2020-2023)
                - Led development of microservices architecture
                - Technologies: Java, Spring Boot, Docker
                
                Education:
                University of Technology - Bachelor of Science in Computer Science (2016-2020)
                GPA: 3.8
                """;

        public static final String INVALID_CV_TEXT = """
                Invalid CV
                No proper structure
                Missing required fields
                """;

        public static final String EMPTY_CV_TEXT = "";
    }
}
