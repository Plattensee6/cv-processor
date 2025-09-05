# CV Processing Application

A Spring Boot application that processes CV documents using Large Language Model (LLM) technology to extract specific fields and validate them according to predefined business rules.

## 🏗️ Architecture

This application follows Clean Architecture + DDD principles with the following layers:
- **Domain Layer**: Core business entities and rules
- **Application Layer**: Use cases and business logic
- **Infrastructure Layer**: External services and data persistence
- **Presentation Layer**: REST API controllers

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- OpenAI API Key

### 1. Start Database

```bash
# Start PostgreSQL using Docker Compose
docker-compose up -d postgres

# Verify database is running
docker-compose ps
```

### 2. Configure Environment

Create a `.env` file in the project root:

```bash
# Database Configuration
POSTGRES_DB=cvprocessor
POSTGRES_USER=cvuser
POSTGRES_PASSWORD=cvpass
POSTGRES_PORT=5432

# OpenAI Configuration
OPENAI_API_KEY=your-openai-api-key-here
OPENAI_MODEL=gpt-4
OPENAI_MAX_TOKENS=2000
OPENAI_TEMPERATURE=0.1

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
FILE_MAX_SIZE=10MB
```

### 3. Build and Run

```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

### 4. Verify Setup

```bash
# Check application health
curl http://localhost:8080/api/health

# Check detailed health
curl http://localhost:8080/api/health/detailed

# Check Spring Boot actuator health
curl http://localhost:8080/actuator/health
```

## 📋 API Endpoints

### Health Check
- `GET /api/health` - Basic health check
- `GET /api/health/detailed` - Detailed health information
- `GET /actuator/health` - Spring Boot actuator health

### File Upload
- `POST /api/cv/upload` - Upload CV file for processing
- `GET /api/cv/status/{requestId}` - Get processing status
- `GET /api/cv/health` - File upload service health check

### CV Processing
- `POST /api/cv/process/{requestId}` - Process CV and extract fields
- `GET /api/cv/process/{requestId}/fields` - Get extracted fields
- `GET /api/cv/process/health` - CV processing service health check

### Complete Processing
- `POST /api/cv/complete/{requestId}` - Complete CV processing (extract + validate)
- `GET /api/cv/complete/{requestId}` - Get complete processing result
- `GET /api/cv/complete/health` - Complete processing service health check

### Metrics
- `GET /api/metrics` - Application metrics
- `GET /api/metrics/detailed` - Detailed metrics
- `GET /api/metrics/health` - Metrics service health check

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

## 🔧 Configuration

### Profiles

- **dev**: Development profile with debug logging and H2 database
- **prod**: Production profile with optimized settings

### Environment Variables

All configuration uses environment variables with sensible defaults:

- `SPRING_PROFILES_ACTIVE`: Active Spring profile (default: dev)
- `POSTGRES_*`: Database connection settings
- `OPENAI_*`: OpenAI API configuration
- `FILE_*`: File upload settings

## 📊 Database Schema

The application uses PostgreSQL with the following main entities:

- `cv_processing_requests`: CV upload metadata and processing status
- `extracted_fields`: Fields extracted from CV documents
- `validation_results`: Validation results for extracted fields

## 🐳 Docker

### Development
```bash
# Start only database
docker-compose up -d postgres

# Start full stack (when implemented)
docker-compose up -d
```

### Production
```bash
# Build application image
docker build -t cv-processor .

# Run with production profile
docker run -e SPRING_PROFILES_ACTIVE=prod cv-processor
```

## 📝 Development Status

### ✅ Milestone 1: Project Setup & Infrastructure
- [x] Maven project with all dependencies
- [x] PostgreSQL + Docker Compose setup
- [x] Spring profiles (dev/prod) with placeholders
- [x] Basic health check endpoint
- [x] Database entity structure
- [x] Application startup and database connection

### ✅ Milestone 2: Document Upload & Storage
- [x] File upload REST endpoint
- [x] File validation (type, size, extension)
- [x] Document parsing (PDF, DOC, DOCX)
- [x] File storage service
- [x] Database persistence of upload metadata

### ✅ Milestone 3: LLM Integration & Field Extraction
- [x] OpenAI client configuration
- [x] Field extraction service
- [x] Prompt building for CV analysis
- [x] Extraction of all 4 fields (Work Experience, Skills, Languages, Profile)
- [x] Error handling for LLM failures

### ✅ Milestone 4: Validation Engine
- [x] Domain validators for each field
- [x] Validation rules implementation
- [x] Validation result aggregation
- [x] Comprehensive error messages

### ✅ Milestone 5: Complete Processing Pipeline
- [x] Complete processing service
- [x] JSON response with extracted fields and validation
- [x] Comprehensive error handling
- [x] Async processing capability

### ✅ Milestone 6: Advanced Features
- [x] Async processing with CompletableFuture
- [x] Metrics and monitoring
- [x] Enhanced error handling
- [x] Performance optimization

### 🎉 All Milestones Completed!
The CV processing application is now production-ready with all features implemented.

## 🤝 Contributing

1. Follow the milestone-based development approach
2. Ensure all quality gates pass before proceeding
3. Write comprehensive tests for new features
4. Update documentation as needed

## 📄 License

This project is part of a coding challenge for IntuiTech.
