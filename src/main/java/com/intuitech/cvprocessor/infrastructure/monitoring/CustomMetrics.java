package com.intuitech.cvprocessor.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom metrics for CV processing application
 * 
 * Provides business-specific metrics and performance monitoring.
 */
@Component
@Slf4j
public class CustomMetrics {

    private final MeterRegistry meterRegistry;
    
    // Counters
    private final Counter cvUploadsTotal;
    private final Counter cvProcessingTotal;
    private final Counter cvProcessingSuccess;
    private final Counter cvProcessingFailures;
    private final Counter validationSuccess;
    private final Counter validationFailures;
    private final Counter llmRequestsTotal;
    private final Counter llmRequestsSuccess;
    private final Counter llmRequestsFailures;

    // Timers
    private final Timer cvProcessingTime;
    private final Timer fileUploadTime;
    private final Timer llmResponseTime;
    private final Timer validationTime;

    // Gauges
    private final AtomicLong activeProcessingRequests = new AtomicLong(0);
    private final AtomicLong totalProcessedCvs = new AtomicLong(0);
    private final AtomicLong totalValidCvs = new AtomicLong(0);
    private final AtomicLong totalInvalidCvs = new AtomicLong(0);

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.cvUploadsTotal = Counter.builder("cv.uploads.total")
                .description("Total number of CV uploads")
                .register(meterRegistry);
                
        this.cvProcessingTotal = Counter.builder("cv.processing.total")
                .description("Total number of CV processing requests")
                .register(meterRegistry);
                
        this.cvProcessingSuccess = Counter.builder("cv.processing.success")
                .description("Successful CV processing requests")
                .register(meterRegistry);
                
        this.cvProcessingFailures = Counter.builder("cv.processing.failures")
                .description("Failed CV processing requests")
                .register(meterRegistry);
                
        this.validationSuccess = Counter.builder("cv.validation.success")
                .description("Successful validations")
                .register(meterRegistry);
                
        this.validationFailures = Counter.builder("cv.validation.failures")
                .description("Failed validations")
                .register(meterRegistry);
                
        this.llmRequestsTotal = Counter.builder("llm.requests.total")
                .description("Total LLM requests")
                .register(meterRegistry);
                
        this.llmRequestsSuccess = Counter.builder("llm.requests.success")
                .description("Successful LLM requests")
                .register(meterRegistry);
                
        this.llmRequestsFailures = Counter.builder("llm.requests.failures")
                .description("Failed LLM requests")
                .register(meterRegistry);

        // Initialize timers
        this.cvProcessingTime = Timer.builder("cv.processing.duration")
                .description("CV processing duration")
                .register(meterRegistry);
                
        this.fileUploadTime = Timer.builder("cv.upload.duration")
                .description("File upload duration")
                .register(meterRegistry);
                
        this.llmResponseTime = Timer.builder("llm.response.duration")
                .description("LLM response time")
                .register(meterRegistry);
                
        this.validationTime = Timer.builder("cv.validation.duration")
                .description("Validation duration")
                .register(meterRegistry);
        
        // Register gauges
        Gauge.builder("cv.processing.active", activeProcessingRequests, AtomicLong::get)
                .description("Number of active processing requests")
                .register(meterRegistry);
                
        Gauge.builder("cv.processed.total", totalProcessedCvs, AtomicLong::get)
                .description("Total number of processed CVs")
                .register(meterRegistry);
                
        Gauge.builder("cv.valid.total", totalValidCvs, AtomicLong::get)
                .description("Total number of valid CVs")
                .register(meterRegistry);
                
        Gauge.builder("cv.invalid.total", totalInvalidCvs, AtomicLong::get)
                .description("Total number of invalid CVs")
                .register(meterRegistry);
    }

    // CV Upload Metrics
    public void recordCvUpload() {
        cvUploadsTotal.increment();
        log.debug("CV upload recorded");
    }

    public Timer.Sample startFileUploadTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordFileUploadTime(Timer.Sample sample) {
        sample.stop(fileUploadTime);
        log.debug("File upload time recorded");
    }

    // CV Processing Metrics
    public void recordCvProcessingStart() {
        cvProcessingTotal.increment();
        activeProcessingRequests.incrementAndGet();
        log.debug("CV processing started");
    }

    public void recordCvProcessingSuccess() {
        cvProcessingSuccess.increment();
        activeProcessingRequests.decrementAndGet();
        totalProcessedCvs.incrementAndGet();
        log.debug("CV processing success recorded");
    }

    public void recordCvProcessingFailure() {
        cvProcessingFailures.increment();
        activeProcessingRequests.decrementAndGet();
        log.debug("CV processing failure recorded");
    }

    public Timer.Sample startCvProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordCvProcessingTime(Timer.Sample sample) {
        sample.stop(cvProcessingTime);
        log.debug("CV processing time recorded");
    }

    // Validation Metrics
    public void recordValidationSuccess() {
        validationSuccess.increment();
        totalValidCvs.incrementAndGet();
        log.debug("Validation success recorded");
    }

    public void recordValidationFailure() {
        validationFailures.increment();
        totalInvalidCvs.incrementAndGet();
        log.debug("Validation failure recorded");
    }

    public Timer.Sample startValidationTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordValidationTime(Timer.Sample sample) {
        sample.stop(validationTime);
        log.debug("Validation time recorded");
    }

    // LLM Metrics
    public void recordLlmRequest() {
        llmRequestsTotal.increment();
        log.debug("LLM request recorded");
    }

    public void recordLlmRequestSuccess() {
        llmRequestsSuccess.increment();
        log.debug("LLM request success recorded");
    }

    public void recordLlmRequestFailure() {
        llmRequestsFailures.increment();
        log.debug("LLM request failure recorded");
    }

    public Timer.Sample startLlmResponseTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordLlmResponseTime(Timer.Sample sample) {
        sample.stop(llmResponseTime);
        log.debug("LLM response time recorded");
    }

    // Business Metrics
    public void recordBusinessMetric(String metricName, double value, String... tags) {
        Gauge.builder("cv.business." + metricName, () -> value)
                .description("Business metric: " + metricName)
                .tags(tags)
                .register(meterRegistry);
        log.debug("Business metric recorded: {} = {}", metricName, value);
    }

    // Get current metrics summary
    public java.util.Map<String, Object> getMetricsSummary() {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("cvUploadsTotal", cvUploadsTotal.count());
        summary.put("cvProcessingTotal", cvProcessingTotal.count());
        summary.put("cvProcessingSuccess", cvProcessingSuccess.count());
        summary.put("cvProcessingFailures", cvProcessingFailures.count());
        summary.put("validationSuccess", validationSuccess.count());
        summary.put("validationFailures", validationFailures.count());
        summary.put("llmRequestsTotal", llmRequestsTotal.count());
        summary.put("llmRequestsSuccess", llmRequestsSuccess.count());
        summary.put("llmRequestsFailures", llmRequestsFailures.count());
        summary.put("activeProcessingRequests", activeProcessingRequests.get());
        summary.put("totalProcessedCvs", totalProcessedCvs.get());
        summary.put("totalValidCvs", totalValidCvs.get());
        summary.put("totalInvalidCvs", totalInvalidCvs.get());
        summary.put("timestamp", LocalDateTime.now());
        return summary;
    }
}
