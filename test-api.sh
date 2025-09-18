#!/bin/bash

# CV Processor API Test Script
# This script tests all the main endpoints of the CV Processor API

BASE_URL="http://localhost:8080"
REQUEST_ID=""
REQUEST_ID2=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if service is running
check_service() {
    print_status "Checking if CV Processor API is running..."
    if curl -s -f "$BASE_URL/api/health" > /dev/null 2>&1; then
        print_success "API is running at $BASE_URL"
        return 0
    else
        print_error "API is not running at $BASE_URL"
        print_status "Please start the application first:"
        print_status "  docker-compose up -d"
        print_status "  or"
        print_status "  mvn spring-boot:run"
        return 1
    fi
}

# Function to test health endpoints
test_health_endpoints() {
    print_status "=== Testing Health Endpoints ==="
    
    # Basic Health Check
    print_status "Testing basic health check..."
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/health")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Basic health check passed"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Basic health check failed (HTTP $http_code)"
    fi
    
    echo ""
    
    # Detailed Health Check
    print_status "Testing detailed health check..."
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/health/detailed")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Detailed health check passed"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Detailed health check failed (HTTP $http_code)"
    fi
    
    echo ""
    
    # Spring Boot Actuator Health
    print_status "Testing Spring Boot actuator health..."
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/actuator/health")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Actuator health check passed"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Actuator health check failed (HTTP $http_code)"
    fi
    
    echo ""
}

# Function to test file upload
test_file_upload() {
    print_status "=== Testing File Upload ==="
    
    # Check if CV files exist
    if [ ! -f "CV-1.pdf" ]; then
        print_warning "CV-1.pdf not found, skipping file upload test"
        return 1
    fi
    
    # Upload CV-1
    print_status "Uploading CV-1.pdf..."
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/cv/upload" -F "file=@CV-1.pdf")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "CV-1 upload successful"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
        REQUEST_ID=$(echo "$body" | jq -r '.requestId' 2>/dev/null)
        if [ "$REQUEST_ID" != "null" ] && [ "$REQUEST_ID" != "" ]; then
            print_success "Request ID: $REQUEST_ID"
        fi
    else
        print_error "CV-1 upload failed (HTTP $http_code)"
        echo "$body"
    fi
    
    echo ""
    
    # Upload CV-2 if exists
    if [ -f "CV-2.pdf" ]; then
        print_status "Uploading CV-2.pdf..."
        response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/cv/upload" -F "file=@CV-2.pdf")
        http_code=$(echo "$response" | tail -n1)
        body=$(echo "$response" | head -n -1)
        
        if [ "$http_code" = "200" ]; then
            print_success "CV-2 upload successful"
            echo "$body" | jq '.' 2>/dev/null || echo "$body"
            REQUEST_ID2=$(echo "$body" | jq -r '.requestId' 2>/dev/null)
            if [ "$REQUEST_ID2" != "null" ] && [ "$REQUEST_ID2" != "" ]; then
                print_success "Request ID 2: $REQUEST_ID2"
            fi
        else
            print_error "CV-2 upload failed (HTTP $http_code)"
            echo "$body"
        fi
        echo ""
    fi
}

# Function to test CV processing
test_cv_processing() {
    print_status "=== Testing CV Processing ==="
    
    if [ -z "$REQUEST_ID" ] || [ "$REQUEST_ID" = "null" ]; then
        print_warning "No valid request ID available, skipping processing test"
        return 1
    fi
    
    # Complete processing
    print_status "Processing CV with request ID: $REQUEST_ID"
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/cv/complete/$REQUEST_ID")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "CV processing completed successfully"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "CV processing failed (HTTP $http_code)"
        echo "$body"
    fi
    
    echo ""
    
    # Get processing result
    print_status "Getting processing result for request ID: $REQUEST_ID"
    response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/cv/complete/$REQUEST_ID")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Processing result retrieved successfully"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Failed to get processing result (HTTP $http_code)"
        echo "$body"
    fi
    
    echo ""
}

# Function to test metrics
test_metrics() {
    print_status "=== Testing Metrics ==="
    
    # Basic metrics
    print_status "Getting application metrics..."
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/metrics")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Metrics retrieved successfully"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Failed to get metrics (HTTP $http_code)"
        echo "$body"
    fi
    
    echo ""
    
    # Detailed metrics
    print_status "Getting detailed metrics..."
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/metrics/detailed")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Detailed metrics retrieved successfully"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Failed to get detailed metrics (HTTP $http_code)"
        echo "$body"
    fi
    
    echo ""
}

# Function to test Ollama endpoints
test_ollama_endpoints() {
    print_status "=== Testing Ollama Endpoints ==="
    
    # Ollama health check
    print_status "Checking Ollama health..."
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/health/ollama")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "503" ]; then
        print_success "Ollama health check completed (HTTP $http_code)"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Ollama health check failed (HTTP $http_code)"
        echo "$body"
    fi
    
    echo ""
    
    # Ollama configuration
    print_status "Getting Ollama configuration..."
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/health/ollama/config")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        print_success "Ollama configuration retrieved successfully"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_error "Failed to get Ollama configuration (HTTP $http_code)"
        echo "$body"
    fi
    
    echo ""
}

# Function to test error handling
test_error_handling() {
    print_status "=== Testing Error Handling ==="
    
    # Test non-existent request
    print_status "Testing non-existent request ID..."
    response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/cv/status/99999")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "404" ]; then
        print_success "Non-existent request handled correctly (HTTP 404)"
    else
        print_warning "Unexpected response for non-existent request (HTTP $http_code)"
    fi
    echo "$body"
    
    echo ""
}

# Main execution
main() {
    echo "=========================================="
    echo "    CV Processor API Test Script"
    echo "=========================================="
    echo ""
    
    # Check if jq is available
    if ! command -v jq &> /dev/null; then
        print_warning "jq is not installed. JSON responses will not be formatted."
        print_status "Install jq for better output formatting:"
        print_status "  - Ubuntu/Debian: sudo apt-get install jq"
        print_status "  - macOS: brew install jq"
        print_status "  - Windows: choco install jq"
        echo ""
    fi
    
    # Check if service is running
    if ! check_service; then
        exit 1
    fi
    
    echo ""
    
    # Run all tests
    test_health_endpoints
    test_file_upload
    test_cv_processing
    test_metrics
    test_ollama_endpoints
    test_error_handling
    
    echo "=========================================="
    print_success "API testing completed!"
    echo "=========================================="
}

# Run main function
main "$@"
