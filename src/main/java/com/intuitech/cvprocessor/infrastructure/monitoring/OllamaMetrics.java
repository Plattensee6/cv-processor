package com.intuitech.cvprocessor.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Metrics collection for Ollama service
 * 
 * Provides comprehensive metrics for monitoring Ollama service performance,
 * including response times, success rates, and error counts.
 */
@Component
@Slf4j
public class OllamaMetrics {
    
    // Counters
    private final Counter ollamaRequestsTotal;
    private final Counter ollamaRequestsSuccess;
    private final Counter ollamaRequestsFailed;
    private final Counter ollamaFallbackTriggered;
    
    // Timers
    private final Timer ollamaResponseTime;
    private final Timer ollamaModelLoadTime;
    
    // Gauges
    private final AtomicLong ollamaServiceStatus = new AtomicLong(0); // 0 = down, 1 = up
    private final AtomicLong ollamaModelStatus = new AtomicLong(0); // 0 = not available, 1 = available
    private final AtomicLong ollamaLastResponseTime = new AtomicLong(0);
    private final AtomicLong ollamaConcurrentRequests = new AtomicLong(0);

    public OllamaMetrics(MeterRegistry meterRegistry) {
        
        // Initialize counters
        this.ollamaRequestsTotal = Counter.builder("ollama_requests_total")
                .description("Total number of Ollama requests")
                .register(meterRegistry);
        
        this.ollamaRequestsSuccess = Counter.builder("ollama_requests_success_total")
                .description("Total number of successful Ollama requests")
                .register(meterRegistry);
        
        this.ollamaRequestsFailed = Counter.builder("ollama_requests_failed_total")
                .description("Total number of failed Ollama requests")
                .register(meterRegistry);
        
        this.ollamaFallbackTriggered = Counter.builder("ollama_fallback_triggered_total")
                .description("Total number of fallback triggers")
                .register(meterRegistry);
        
        
        // Initialize timers
        this.ollamaResponseTime = Timer.builder("ollama_response_time_seconds")
                .description("Ollama response time in seconds")
                .register(meterRegistry);
        
        this.ollamaModelLoadTime = Timer.builder("ollama_model_load_time_seconds")
                .description("Ollama model load time in seconds")
                .register(meterRegistry);
        
        // Register gauges
        Gauge.builder("ollama_service_status", ollamaServiceStatus, AtomicLong::get)
                .description("Ollama service status (0=down, 1=up)")
                .register(meterRegistry);
        
        Gauge.builder("ollama_model_status", ollamaModelStatus, AtomicLong::get)
                .description("Ollama model availability status (0=not available, 1=available)")
                .register(meterRegistry);
        
        Gauge.builder("ollama_last_response_time_ms", ollamaLastResponseTime, AtomicLong::get)
                .description("Last Ollama response time in milliseconds")
                .register(meterRegistry);
        
        Gauge.builder("ollama_concurrent_requests", ollamaConcurrentRequests, AtomicLong::get)
                .description("Current number of concurrent Ollama requests")
                .register(meterRegistry);
    }

    /**
     * Record a successful Ollama request
     */
    public void recordSuccess() {
        ollamaRequestsTotal.increment();
        ollamaRequestsSuccess.increment();
        log.debug("Recorded successful Ollama request");
    }

    /**
     * Record a failed Ollama request
     */
    public void recordFailure() {
        ollamaRequestsTotal.increment();
        ollamaRequestsFailed.increment();
        log.debug("Recorded failed Ollama request");
    }

    /**
     * Record response time for Ollama request
     * 
     * @param responseTimeMs response time in milliseconds
     */
    public void recordResponseTime(long responseTimeMs) {
        ollamaResponseTime.record(responseTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        ollamaLastResponseTime.set(responseTimeMs);
        log.debug("Recorded Ollama response time: {}ms", responseTimeMs);
    }

    /**
     * Record model load time
     * 
     * @param loadTimeMs model load time in milliseconds
     */
    public void recordModelLoadTime(long loadTimeMs) {
        ollamaModelLoadTime.record(loadTimeMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        log.debug("Recorded Ollama model load time: {}ms", loadTimeMs);
    }

    /**
     * Record fallback trigger
     */
    public void recordFallbackTriggered() {
        ollamaFallbackTriggered.increment();
        log.debug("Recorded Ollama fallback triggered");
    }

    /**
     * Update service status
     * 
     * @param isUp true if service is up, false otherwise
     */
    public void updateServiceStatus(boolean isUp) {
        ollamaServiceStatus.set(isUp ? 1 : 0);
        log.debug("Updated Ollama service status: {}", isUp ? "UP" : "DOWN");
    }

    /**
     * Update model availability status
     * 
     * @param isAvailable true if model is available, false otherwise
     */
    public void updateModelStatus(boolean isAvailable) {
        ollamaModelStatus.set(isAvailable ? 1 : 0);
        log.debug("Updated Ollama model status: {}", isAvailable ? "AVAILABLE" : "NOT_AVAILABLE");
    }

    /**
     * Increment concurrent requests counter
     */
    public void incrementConcurrentRequests() {
        ollamaConcurrentRequests.incrementAndGet();
        log.debug("Incremented concurrent requests: {}", ollamaConcurrentRequests.get());
    }

    /**
     * Decrement concurrent requests counter
     */
    public void decrementConcurrentRequests() {
        ollamaConcurrentRequests.decrementAndGet();
        log.debug("Decremented concurrent requests: {}", ollamaConcurrentRequests.get());
    }

    /**
     * Get current metrics summary
     * 
     * @return metrics summary map
     */
    public java.util.Map<String, Object> getMetricsSummary() {
        return java.util.Map.of(
            "totalRequests", ollamaRequestsTotal.count(),
            "successfulRequests", ollamaRequestsSuccess.count(),
            "failedRequests", ollamaRequestsFailed.count(),
            "fallbackTriggered", ollamaFallbackTriggered.count(),
            "serviceStatus", ollamaServiceStatus.get() == 1 ? "UP" : "DOWN",
            "modelStatus", ollamaModelStatus.get() == 1 ? "AVAILABLE" : "NOT_AVAILABLE",
            "lastResponseTimeMs", ollamaLastResponseTime.get(),
            "concurrentRequests", ollamaConcurrentRequests.get(),
            "averageResponseTimeMs", ollamaResponseTime.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
        );
    }

    /**
     * Reset all metrics (useful for testing)
     */
    public void reset() {
        ollamaServiceStatus.set(0);
        ollamaModelStatus.set(0);
        ollamaLastResponseTime.set(0);
        ollamaConcurrentRequests.set(0);
        log.info("Reset all Ollama metrics");
    }
}
