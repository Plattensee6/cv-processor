package com.intuitech.cvprocessor.util;

import com.intuitech.cvprocessor.domain.model.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
                .status(CVProcessingRequest.ProcessingStatus.UPLOADED)
                .build();
    }

    public static CVProcessingRequest createCompletedRequest() {
        return TestDataBuilder.cvProcessingRequest()
                .fileName("jane-smith-cv.pdf")
                .contentType("application/pdf")
                .fileSize(1536L)
                .status(CVProcessingRequest.ProcessingStatus.COMPLETED)
                .parsedText("Jane Smith\nSenior Developer\n8 years experience in Java")
                .build();
    }

    public static CVProcessingRequest createFailedRequest() {
        return TestDataBuilder.cvProcessingRequest()
                .fileName("invalid-file.txt")
                .contentType("text/plain")
                .fileSize(512L)
                .status(CVProcessingRequest.ProcessingStatus.FAILED)
                .errorMessage("Unsupported file type")
                .build();
    }

    // Extracted Fields mocks
    public static ExtractedFields createValidExtractedFields() {
        return TestDataBuilder.extractedFields()
                .workExperienceYears(5)
                .workExperienceDetails("Senior Software Engineer at Tech Corp (2020-2023) - Led development of microservices architecture using Java, Spring Boot, and Docker")
                .skills("Java, Spring Boot, PostgreSQL, Docker, Kubernetes")
                .languages("English, Spanish")
                .profile("Experienced software engineer with 5+ years in Java development. Bachelor of Science in Computer Science from University of Technology (2016-2020) with GPA 3.8")
                .build();
    }

    public static ExtractedFields createIncompleteExtractedFields() {
        return TestDataBuilder.extractedFields()
                .workExperienceYears(2)
                .workExperienceDetails("Developer at Test Company (2021-2023)")
                .skills("Python, Django")
                .languages("English")
                .profile("Developer with some experience")
                .build();
    }

    public static ExtractedFields createInvalidExtractedFields() {
        return TestDataBuilder.extractedFields()
                .workExperienceYears(0)
                .workExperienceDetails("") // Invalid: empty work experience
                .skills("") // Invalid: empty skills
                .languages("") // Invalid: empty languages
                .profile("") // Invalid: empty profile
                .build();
    }

    // Validation Result mocks
    public static ValidationResult createValidValidationResult() {
        return TestDataBuilder.validationResult()
                .overallValid(true)
                .workExperienceValid(true)
                .workExperienceMessage("Work experience validation successful")
                .skillsValid(true)
                .skillsMessage("Skills validation successful")
                .languagesValid(true)
                .languagesMessage("Languages validation successful")
                .profileValid(true)
                .profileMessage("Profile validation successful")
                .errors(List.of())
                .warnings(List.of("Phone number format could be improved"))
                .build();
    }

    public static ValidationResult createInvalidValidationResult() {
        return TestDataBuilder.validationResult()
                .overallValid(false)
                .workExperienceValid(false)
                .workExperienceMessage("Work experience validation failed")
                .skillsValid(false)
                .skillsMessage("Skills validation failed")
                .languagesValid(true)
                .languagesMessage("Languages validation successful")
                .profileValid(false)
                .profileMessage("Profile validation failed")
                .errors(List.of(
                        "Work experience is required",
                        "Skills are required",
                        "Profile is required"
                ))
                .warnings(List.of())
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
        
        fields.setCvProcessingRequest(request);
        result.setExtractedFields(fields);
        
        return new CompleteProcessingPipeline(request, fields, result);
    }

    public static CompleteProcessingPipeline createFailedPipeline() {
        CVProcessingRequest request = createFailedRequest();
        ExtractedFields fields = createInvalidExtractedFields();
        ValidationResult result = createInvalidValidationResult();
        
        fields.setCvProcessingRequest(request);
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
