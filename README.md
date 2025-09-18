# CV Processing Application

A Spring Boot application that processes CV documents using Large Language Model (LLM) technology to extract specific fields and validate them according to predefined business rules.

## Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **PostgreSQL** (automatically started with Docker Compose)
- **Ollama** (local LLM model, no API key required)

## API Documentation

### Swagger UI
Interactive API documentation is available at:
- **Local Development**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

For detailed API documentation, see [docs/api-documentation.md](docs/api-documentation.md).

## Postman Testing

### 1. Import Postman Collection

1. Open the Postman application
2. Click the **Import** button
3. Select the `CV-Processor-API.postman_collection.json` file from the project root directory
   - **File location**: `./CV-Processor-API.postman_collection.json` (project root directory)
4. The collection will be automatically imported

### 2. Set Environment Variables

1. In Postman, select the **CV Processor API** collection
2. Click the **Variables** tab
3. Set the following variable:
   - `baseUrl`: `http://localhost:8080`

### 3. Start the Application

```bash
# Start full environment (database + Ollama)
docker-compose up -d

```

### 4. Testing Workflow

**Note:** You can automatically test all endpoints using the provided test scripts (see [Automated API Testing](#automated-api-testing) section below).

#### Basic health check
1. **Health Check** → `GET /api/health`
2. **Detailed Health** → `GET /api/health/detailed`

#### CV processing testing
1. **File Upload** → `POST /api/cv/upload`
   - Select the `CV-1.pdf` or `CV-2.pdf` file from the project root directory
   - The response will contain a `requestId`

2. **Processing Status** → `GET /api/cv/status/{requestId}`
   - Use the `requestId` from the previous step
   - Check that the status is `UPLOADED` and file is parsed

3. **Process CV** → `POST /api/cv/process/{requestId}`
   - Use the same `requestId` from step 1
   - This triggers the LLM field extraction and validation
   - The response will show processing status

4. **Check Processing Status** → `GET /api/cv/status/{requestId}`
   - Use the same `requestId`
   - Repeat until the status becomes `COMPLETED`

5. **Get Extracted Fields** → `GET /api/cv/process/{requestId}/fields`
   - Use the same `requestId`
   - Retrieves the extracted and validated field data

## Automated API Testing

### Using the Test Script

Comprehensive test scripts are provided to automatically test all API endpoints:

#### Linux/macOS (Bash)
```bash
# Make the script executable
chmod +x test-api.sh

# Run the test script
./test-api.sh
```

#### Windows (PowerShell)
```powershell
# Run the PowerShell test script
.\test-api.ps1

# Or with custom base URL
.\test-api.ps1 -BaseUrl "http://localhost:8080"
```

### What the Scripts Test

Both test scripts (`test-api.sh` and `test-api.ps1`) automatically test:

1. **Health Endpoints**
   - Basic health check (`/api/health`)
   - Detailed health check (`/api/health/detailed`)
   - Spring Boot actuator health (`/actuator/health`)

2. **File Upload**
   - Uploads `CV-1.pdf` (Marketing Manager)
   - Uploads `CV-2.pdf` (Software Developer) if available
   - Validates upload responses and extracts request IDs

3. **CV Processing**
   - Complete processing workflow (extract + validate)
   - Retrieves processing results
   - Tests with real PDF files

4. **Metrics & Monitoring**
   - Application metrics (`/api/metrics`)
   - Detailed metrics (`/api/metrics/detailed`)

5. **Ollama Integration**
   - Ollama health check (`/api/health/ollama`)
   - Ollama configuration (`/api/health/ollama/config`)

6. **Error Handling**
   - Non-existent request ID handling
   - Invalid endpoint testing

### Prerequisites for Testing

- **jq** (optional, for JSON formatting): Install with `sudo apt-get install jq` (Ubuntu) or `brew install jq` (macOS)
- **CV sample files**: `CV-1.pdf` and `CV-2.pdf` in the project root
- **Running application**: Start with `docker-compose up -d` or `mvn spring-boot:run`

### Manual Curl Testing

You can also test individual endpoints manually:

```bash
# Health check
curl -X GET "http://localhost:8080/api/health"

# Upload CV file
curl -X POST "http://localhost:8080/api/cv/upload" -F "file=@CV-1.pdf"

# Complete processing (replace REQUEST_ID with actual ID)
curl -X POST "http://localhost:8080/api/cv/complete/REQUEST_ID"

# Get metrics
curl -X GET "http://localhost:8080/api/metrics"
```

### Example Test Output

When you run the test script, you'll see output like this:

```
==========================================
    CV Processor API Test Script
==========================================

[INFO] Checking if CV Processor API is running...
[SUCCESS] API is running at http://localhost:8080

[INFO] === Testing Health Endpoints ===
[INFO] Testing basic health check...
[SUCCESS] Basic health check passed
{
  "status": "UP",
  "application": "CV Processor",
  "version": "1.0.0",
  "timestamp": "2025-01-09T10:30:00Z"
}

[INFO] === Testing File Upload ===
[INFO] Uploading CV-1.pdf...
[SUCCESS] CV-1 upload successful
[SUCCESS] Request ID: 12345

[INFO] === Testing CV Processing ===
[INFO] Processing CV with request ID: 12345
[SUCCESS] CV processing completed successfully
{
  "requestId": 12345,
  "status": "COMPLETED",
  "extractedFields": {
    "workExperienceYears": 2,
    "skills": "Java, Spring Boot, LLM, Machine Learning",
    "languages": "Hungarian, English",
    "profile": "Experienced software developer with interest in GenAI and Java"
  },
  "validationResult": {
    "workExperienceValid": true,
    "skillsValid": true,
    "languagesValid": true,
    "profileValid": true,
    "overallValid": true
  }
}

==========================================
[SUCCESS] API testing completed!
==========================================
```

## Features

### CV Processing
- **Supported formats**: PDF, DOC, DOCX
- **Automatic text extraction**: Using Apache Tika
- **LLM-based field extraction**: With Ollama local models
- **Validation**: According to business rules

### Extracted fields
- **Work experience**: Years and details
- **Skills**: Technical and professional skills
- **Languages**: List of known languages
- **Profile**: Summary description

### Monitoring and metrics
- **Spring Actuator**: Health checks and metrics
- **Structured logging**: In JSON format
- **Performance metrics**: Processing time and success rate

## Configuration

### Environment variables

```bash
# Database
POSTGRES_DB=cvprocessor
POSTGRES_USER=cvuser
POSTGRES_PASSWORD=cvpass
POSTGRES_PORT=5432

# Ollama (local LLM, no API key required)
OLLAMA_HOST=localhost
OLLAMA_PORT=11434
OLLAMA_MODEL=llama3.2:3b
OLLAMA_TIMEOUT=120

# Application
SPRING_PROFILES_ACTIVE=dev
FILE_MAX_SIZE=10MB
```

### Profiles
- **dev**: Development environment with debug logging
- **prod**: Production environment with optimized settings
- **test**: Test environment with mock services

## Technology Stack

- **Backend**: Spring Boot 3.x, Java 21
- **Database**: PostgreSQL 15+
- **LLM**: Ollama (local model)
- **Document processing**: Apache Tika
- **Build tool**: Maven 3.8+
- **Containerization**: Docker & Docker Compose
- **Monitoring**: Spring Actuator, Micrometer

## License
This project is for internal development use.
