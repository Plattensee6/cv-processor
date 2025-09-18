# 2. SZAKASZ: Ollama Integráció Optimalizálása

## 🎯 Cél
Az Ollama integráció teljes funkcionalitásának biztosítása és optimalizálása.

## 📋 Funkciók

### 2.1 Automatikus Modell Letöltés
- Ollama konténer indításkor modell letöltése
- Modell verzió kezelés
- Modell cache mechanizmus
- Modell health check

### 2.2 Service Health Monitoring
- Ollama service elérhetőség ellenőrzése
- Modell betöltöttség ellenőrzése
- Performance metrikák gyűjtése
- Health status reporting

### 2.3 Fallback Mechanizmus
- Smooth fallback HuggingFace-re
- Circuit breaker pattern
- Retry logic optimalizálás
- Error recovery

### 2.4 Performance Optimalizálás
- Response time monitoring
- Memory usage tracking
- Concurrent request handling
- Caching implementálás

## 🔧 Követelmények

### 2.1 Modell Letöltés
- **Automatikus**: Indításkor modell letöltése
- **Verzió kezelés**: Modell verzió tracking
- **Cache**: Modell cache mechanizmus
- **Health Check**: Modell elérhetőség ellenőrzése

### 2.2 Service Monitoring
- **Health Endpoint**: `/api/health/ollama`
- **Metrics**: Response time, success rate
- **Status**: Service status reporting
- **Alerts**: Critical error alerting

### 2.3 Fallback Logic
- **Primary**: Ollama service
- **Fallback**: HuggingFace API
- **Circuit Breaker**: Failure threshold
- **Recovery**: Automatic recovery

### 2.4 Performance
- **Response Time**: <2s average
- **Memory**: <1GB usage
- **Concurrency**: 10 concurrent requests
- **Caching**: Response caching

## 📁 Deliverables

### 2.1 Docker Konfiguráció
```
docker/
├── init-ollama.sh
├── ollama-healthcheck.sh
└── model-download.sh
```

### 2.2 Service Implementáció
```
src/main/java/com/intuitech/cvprocessor/
├── infrastructure/
│   ├── service/
│   │   ├── OllamaFieldExtractor.java
│   │   ├── OllamaHealthService.java
│   │   └── OllamaModelService.java
│   └── config/
│       └── OllamaConfig.java
└── application/
    └── service/
        └── ModelManagementService.java
```

### 2.3 Monitoring
```
src/main/java/com/intuitech/cvprocessor/
├── infrastructure/
│   ├── monitoring/
│   │   ├── OllamaMetrics.java
│   │   └── OllamaHealthIndicator.java
│   └── controller/
│       └── OllamaHealthController.java
```

## 🚀 Implementáció Lépések

### 2.1 Modell Letöltés (2 nap)
- `init-ollama.sh` script létrehozása
- Modell verzió kezelés
- Cache mechanizmus

### 2.2 Health Monitoring (1 nap)
- Health check endpoint
- Service status monitoring
- Metrics gyűjtés

### 2.3 Fallback Logic (1 nap)
- Circuit breaker optimalizálás
- Retry logic javítás
- Error recovery

### 2.4 Performance (1 nap)
- Response time monitoring
- Memory usage tracking
- Caching implementálás

## 📊 Success Criteria

- **Modell Letöltés**: Automatikus indításkor
- **Health Check**: Service elérhetőség
- **Fallback**: Smooth HuggingFace fallback
- **Performance**: <2s response time
- **Monitoring**: Real-time metrics

## 🔍 Acceptance Criteria

- [ ] Ollama konténer indításkor modell letöltődik
- [ ] Health check endpoint működik
- [ ] Fallback mechanizmus tesztelt
- [ ] Performance metrikák gyűjtődnek
- [ ] Circuit breaker proper működik
- [ ] Error recovery automatikus
- [ ] Monitoring dashboard elérhető
- [ ] Alerting konfigurálva
