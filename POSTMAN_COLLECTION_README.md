# CV Processor API - Postman Collection

## 📋 Overview

This Postman collection provides comprehensive testing capabilities for the CV Processor API, including all endpoints for file upload, CV processing, field extraction, validation, and monitoring.

## 🚀 Quick Start

### 1. Import Collection
1. Open Postman
2. Click "Import" button
3. Select the `CV-Processor-API.postman_collection.json` file
4. The collection will be imported with all endpoints and test scenarios

### 2. Set Environment Variables
The collection uses the following variables:
- `baseUrl`: Base URL for the API (default: `http://localhost:8080`)
- `requestId`: Automatically set during file uploads
- `cv1RequestId`: Set during CV-1 workflow tests
- `cv2RequestId`: Set during CV-2 workflow tests

### 3. Prepare Test Files
Ensure you have the following files available for testing:
- `CV-1.md` - Marketing Manager CV (expected to fail validation)
- `CV-2.md` - Software Developer CV (expected to pass validation)

## 📁 Collection Structure

### 🏥 Health Checks
- **Basic Health Check** (`GET /api/health`)
- **Detailed Health Check** (`GET /api/health/detailed`)
- **Spring Boot Actuator Health** (`GET /actuator/health`)

### 📁 File Upload
- **Upload CV File** (`POST /api/cv/upload`)
- **Get Upload Status** (`GET /api/cv/status/{requestId}`)
- **File Upload Service Health** (`GET /api/cv/health`)

### 🔄 CV Processing
- **Process CV (Extract Fields)** (`POST /api/cv/process/{requestId}`)
- **Get Extracted Fields** (`GET /api/cv/process/{requestId}/fields`)
- **CV Processing Service Health** (`GET /api/cv/process/health`)

### ✅ Complete Processing
- **Complete CV Processing** (`POST /api/cv/complete/{requestId}`)
- **Get Complete Processing Result** (`GET /api/cv/complete/{requestId}`)
- **Complete Processing Service Health** (`GET /api/cv/complete/health`)

### 📊 Metrics & Monitoring
- **Get Application Metrics** (`GET /api/metrics`)
- **Get Detailed Metrics** (`GET /api/metrics/detailed`)
- **Metrics Service Health** (`GET /api/metrics/health`)
- **Spring Boot Actuator Metrics** (`GET /actuator/metrics`)
- **Prometheus Metrics** (`GET /actuator/prometheus`)

### 🧪 Test Scenarios
- **Full CV Processing Workflow** - Complete testing with both CV samples
- **Error Handling Tests** - Edge cases and error scenarios

## 🎯 Recommended Testing Workflow

### 1. Health Check
Start by running the health check endpoints to ensure the application is running:
```
GET /api/health
GET /api/health/detailed
```

### 2. Full Workflow Test
Run the "Full CV Processing Workflow" folder to test the complete pipeline:

1. **Upload CV-1** (Marketing Manager)
   - Expected: Upload successful, but validation should fail
   - Missing: Java skills, GenAI interest

2. **Complete Process CV-1**
   - Expected: Processing successful, validation failures for skills and profile

3. **Upload CV-2** (Software Developer)
   - Expected: Upload successful, validation should pass
   - Contains: Java skills, GenAI interest, Hungarian/English languages

4. **Complete Process CV-2**
   - Expected: Processing successful, all validations pass

5. **Compare Results**
   - Check metrics to see validation success rates

### 3. Error Handling Tests
Run the "Error Handling Tests" folder to verify robust error handling:
- Invalid file type upload
- Non-existent request processing
- Non-existent status requests

## 🔧 Automated Features

### Pre-request Scripts
- Automatic timestamp setting
- Request logging for debugging

### Test Scripts
- Response time validation (< 10 seconds)
- JSON response validation
- Automatic requestId extraction and storage
- Comprehensive logging

### Validation Tests
Each endpoint includes specific validation tests:
- Status code verification
- Response structure validation
- Business logic verification
- Error handling validation

## 📊 Expected Results

### CV-1 (Marketing Manager)
- ✅ Upload: Successful
- ✅ Processing: Successful
- ❌ Validation: **FAIL** (missing Java skills, GenAI interest)
- ❌ Overall: **INVALID**

### CV-2 (Software Developer)
- ✅ Upload: Successful
- ✅ Processing: Successful
- ✅ Validation: **PASS** (all requirements met)
- ✅ Overall: **VALID**

## 🐛 Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure the application is running on `http://localhost:8080`
   - Check if the database is running (`docker-compose up -d postgres`)

2. **File Upload Errors**
   - Ensure CV-1.md and CV-2.md files are available
   - Check file permissions and paths

3. **LLM Processing Errors**
   - Verify Hugging Face API key is configured
   - Check network connectivity to LLM services

4. **Validation Failures**
   - Review the validation rules in the Coding_Challenge.md
   - Check extracted fields in the response

### Debug Information
The collection includes comprehensive logging:
- Request/response details
- Validation results
- Error messages
- Performance metrics

## 📈 Performance Expectations

- **Response Time**: < 10 seconds for most operations
- **File Upload**: < 5 seconds
- **LLM Processing**: 5-15 seconds (depending on model)
- **Validation**: < 1 second

## 🔄 Continuous Testing

The collection can be used for:
- **Development Testing**: Verify new features
- **Regression Testing**: Ensure existing functionality works
- **Performance Testing**: Monitor response times
- **Integration Testing**: Test complete workflows

## 📝 Notes

- The collection automatically manages requestId variables
- All tests include proper error handling
- Results are logged for easy debugging
- The collection supports both manual and automated testing

## 🤝 Contributing

To extend the collection:
1. Add new endpoints following the existing pattern
2. Include proper test scripts
3. Update this README with new features
4. Ensure all tests pass before committing

---

**Happy Testing! 🚀**
