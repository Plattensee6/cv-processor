# Multi-stage build for CV Processor Application
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21-jdk-slim

# Install curl and jq for health checks and JSON processing
RUN apt-get update && apt-get install -y curl jq && rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/cv-processor-*.jar app.jar

# Change ownership to app user
RUN chown appuser:appuser app.jar

# Switch to non-root user
USER appuser

# Expose ports
EXPOSE 8080
EXPOSE 5005

# Health check with Ollama dependency check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health && \
      curl -f http://localhost:8080/api/health/ollama/ready || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $DEBUG_OPTS -jar app.jar"]
