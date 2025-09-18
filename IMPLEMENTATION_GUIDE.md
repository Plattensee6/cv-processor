# Implementációs Útmutató

## 🚀 Gyors Indítás

### 1. Előkészítés
```bash
# Projekt klónozása
git clone <repository-url>
cd cv-processor

# Dependencies telepítése
mvn clean install

# Docker konténerek indítása
docker-compose up -d
```

### 2. Ollama Setup
```bash
# Ollama konténer indítása
docker-compose up -d ollama

# Modell letöltése
docker-compose exec ollama ollama pull llama3.2:3b

# Modell tesztelése
docker-compose exec ollama ollama run llama3.2:3b "Hello, how are you?"
```

### 3. Alkalmazás indítása
```bash
# Teljes stack indítása
docker-compose up -d

# Logok ellenőrzése
docker-compose logs -f cv-processor-app
```

## 📋 Fejlesztési Workflow

### 1. Feature Branch
```bash
# Új feature branch
git checkout -b feature/phase-1-testing

# Fejlesztés
# ... kód módosítások ...

# Commit
git add .
git commit -m "feat: implement unit tests for service layer"

# Push
git push origin feature/phase-1-testing
```

### 2. Pull Request
```bash
# Pull request létrehozása
# - Title: [Phase 1] Implement unit tests
# - Description: Detailed description of changes
# - Reviewers: Assign reviewers
# - Labels: phase-1, testing
```

### 3. Code Review
```bash
# Review process
# - Code quality check
# - Test coverage verification
# - Performance impact assessment
# - Security review
```

## 🧪 Tesztelési Stratégia

### 1. Unit Tesztek
```bash
# Unit tesztek futtatása
mvn test

# Coverage report
mvn jacoco:report
```

### 2. Integration Tesztek
```bash
# Integration tesztek
mvn verify

# TestContainers tesztek
mvn test -Dtest=*IntegrationTest
```

### 3. End-to-End Tesztek
```bash
# E2E tesztek
mvn test -Dtest=*E2ETest

# Selenium tesztek
mvn test -Dtest=*SeleniumTest
```

## 📊 Monitoring és Metrikák

### 1. Application Metrics
```bash
# Prometheus metrikák
curl http://localhost:8080/actuator/prometheus

# Health check
curl http://localhost:8080/actuator/health
```

### 2. Grafana Dashboard
```bash
# Grafana indítása
docker-compose up -d grafana

# Dashboard elérése
open http://localhost:3000
```

### 3. Logs
```bash
# Alkalmazás logok
docker-compose logs -f cv-processor-app

# Ollama logok
docker-compose logs -f ollama

# PostgreSQL logok
docker-compose logs -f postgres
```

## 🔧 Konfiguráció

### 1. Environment Variables
```bash
# .env fájl létrehozása
cp env.example .env

# Szerkesztés
nano .env
```

### 2. Application Properties
```yaml
# src/main/resources/application.yml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

### 3. Docker Compose
```yaml
# docker-compose.yml
services:
  cv-processor-app:
    environment:
      - SPRING_PROFILES_ACTIVE=dev
```

## 🚨 Hibaelhárítás

### 1. Gyakori Problémák

#### Ollama nem elérhető
```bash
# Konténer állapot ellenőrzése
docker-compose ps ollama

# Logok megtekintése
docker-compose logs ollama

# Újraindítás
docker-compose restart ollama
```

#### Database kapcsolat hiba
```bash
# PostgreSQL állapot
docker-compose ps postgres

# Kapcsolat tesztelése
docker-compose exec postgres psql -U cvuser -d cvprocessor -c "SELECT 1;"
```

#### Memory probléma
```bash
# Memory használat ellenőrzése
docker stats

# JVM heap dump
docker-compose exec cv-processor-app jcmd 1 GC.run_finalization
```

### 2. Debug Mód
```bash
# Debug port engedélyezése
export DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Alkalmazás újraindítása
docker-compose restart cv-processor-app
```

## 📈 Performance Optimalizálás

### 1. JVM Tuning
```bash
# JVM paraméterek
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"

# GC logging
export JAVA_OPTS="$JAVA_OPTS -XX:+PrintGC -XX:+PrintGCDetails"
```

### 2. Database Optimalizálás
```sql
-- Index létrehozása
CREATE INDEX idx_cv_processing_requests_status ON cv_processing_requests(status);

-- Query optimalizálás
EXPLAIN ANALYZE SELECT * FROM cv_processing_requests WHERE status = 'COMPLETED';
```

### 3. Caching
```yaml
# Redis konfiguráció
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
```

## 🔒 Biztonság

### 1. API Security
```yaml
# Security konfiguráció
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
```

### 2. Input Validation
```java
// Validation annotation
@Valid
@RequestBody FileUploadRequest request
```

### 3. Error Handling
```java
// Custom exception
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        // Error handling
    }
}
```

## 📚 Dokumentáció

### 1. API Dokumentáció
```bash
# Swagger UI
open http://localhost:8080/swagger-ui.html

# OpenAPI spec
curl http://localhost:8080/v3/api-docs
```

### 2. Code Dokumentáció
```bash
# JavaDoc generálása
mvn javadoc:javadoc

# Dokumentáció megtekintése
open target/site/apidocs/index.html
```

### 3. README
```markdown
# CV Processor

## Leírás
Automatikus CV feldolgozó alkalmazás Ollama integrációval.

## Telepítés
```bash
docker-compose up -d
```

## Használat
```bash
curl -X POST http://localhost:8080/api/cv/upload \
  -F "file=@cv.pdf"
```
```
