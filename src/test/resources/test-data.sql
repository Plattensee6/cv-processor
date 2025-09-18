-- Test data for CV Processor application
-- This file contains test data for integration tests

-- Insert test CV processing requests
INSERT INTO cv_processing_requests (id, file_name, content_type, file_size, original_content, parsed_text, status, error_message, created_at, updated_at) VALUES
(1, 'test-cv-1.pdf', 'application/pdf', 2048, 'Original PDF content 1', 'John Doe\nSoftware Engineer\n5 years experience', 'PENDING', NULL, NOW(), NOW()),
(2, 'test-cv-2.doc', 'application/msword', 1536, 'Original DOC content 2', 'Jane Smith\nSenior Developer\n8 years experience', 'COMPLETED', NULL, NOW(), NOW()),
(3, 'test-cv-3.pdf', 'application/pdf', 1024, 'Original PDF content 3', 'Bob Wilson\nJunior Developer\n2 years experience', 'FAILED', 'Parsing error', NOW(), NOW()),
(4, 'test-cv-4.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 3072, 'Original DOCX content 4', 'Alice Johnson\nProject Manager\n10 years experience', 'EXTRACTING', NULL, NOW(), NOW());

-- Insert test extracted fields
INSERT INTO extracted_fields (id, cv_processing_request_id, full_name, email, phone, address, summary, skills, languages, work_experience, education, created_at, updated_at) VALUES
(1, 2, 'Jane Smith', 'jane.smith@example.com', '+1234567890', '123 Main St, New York, NY 10001', 'Experienced software engineer with 8+ years in Java development', 
 '["Java", "Spring Boot", "PostgreSQL", "Docker", "Kubernetes"]', 
 '["English", "Spanish"]', 
 '[{"company": "Tech Corp", "position": "Senior Software Engineer", "startDate": "2020-01-01", "endDate": "2023-12-31", "description": "Led development of microservices architecture", "technologies": ["Java", "Spring Boot", "Docker"]}]',
 '[{"institution": "University of Technology", "degree": "Bachelor of Science", "field": "Computer Science", "startDate": "2016-09-01", "endDate": "2020-06-30", "gpa": "3.8"}]',
 NOW(), NOW());

-- Insert test validation results
INSERT INTO validation_results (id, extracted_fields_id, work_experience_valid, work_experience_message, skills_valid, skills_message, languages_valid, languages_message, profile_valid, profile_message, overall_valid, validation_message, errors, warnings, created_at, updated_at) VALUES
(1, 1, true, 'Work experience is valid', true, 'Skills are valid', true, 'Languages are valid', true, 'Profile is valid', true, 'All validations passed', 
 '[]', 
 '["Phone number format could be improved"]', 
 NOW(), NOW());

-- Reset sequences to avoid conflicts
SELECT setval('cv_processing_requests_id_seq', (SELECT MAX(id) FROM cv_processing_requests));
SELECT setval('extracted_fields_id_seq', (SELECT MAX(id) FROM extracted_fields));
SELECT setval('validation_results_id_seq', (SELECT MAX(id) FROM validation_results));
