package com.intuitech.cvprocessor.infrastructure.service;

import com.intuitech.cvprocessor.infrastructure.monitoring.CustomMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for managing application metrics
 * 
 * Provides methods for recording metrics throughout the application.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final CustomMetrics customMetrics;

    /**
     * Record CV processing request
     */
    public void recordCVProcessingRequest() {
        customMetrics.recordCvProcessingStart();
    }

    /**
     * Record successful CV processing
     */
    public void recordCVProcessingSuccess() {
        customMetrics.recordCvProcessingSuccess();
    }

    /**
     * Record failed CV processing
     */
    public void recordCVProcessingFailure() {
        customMetrics.recordCvProcessingFailure();
    }

    /**
     * Record CV processing duration
     * 
     * @param durationMs processing duration in milliseconds
     */
    public void recordCVProcessingDuration(long durationMs) {
        // Duration is recorded via timer samples in the service layer
        log.debug("Recorded CV processing duration: {} ms", durationMs);
    }

    /**
     * Record file upload
     */
    public void recordFileUpload() {
        customMetrics.recordCvUpload();
    }

    /**
     * Record successful validation
     */
    public void recordValidationSuccess() {
        customMetrics.recordValidationSuccess();
    }

    /**
     * Record failed validation
     */
    public void recordValidationFailure() {
        customMetrics.recordValidationFailure();
    }

    /**
     * Get processing success rate
     * 
     * @return success rate as percentage
     */
    public double getProcessingSuccessRate() {
        java.util.Map<String, Object> metrics = customMetrics.getMetricsSummary();
        double total = (Double) metrics.get("cvProcessingTotal");
        double success = (Double) metrics.get("cvProcessingSuccess");
        
        if (total == 0) {
            return 0.0;
        }
        return (success / total) * 100.0;
    }

    /**
     * Get validation success rate
     * 
     * @return success rate as percentage
     */
    public double getValidationSuccessRate() {
        java.util.Map<String, Object> metrics = customMetrics.getMetricsSummary();
        double success = (Double) metrics.get("validationSuccess");
        double failures = (Double) metrics.get("validationFailures");
        double total = success + failures;
        
        if (total == 0) {
            return 0.0;
        }
        return (success / total) * 100.0;
    }
}
