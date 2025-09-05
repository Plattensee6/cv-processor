package com.intuitech.cvprocessor.infrastructure.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
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

    private final Counter cvProcessingRequestsCounter;
    private final Counter cvProcessingSuccessCounter;
    private final Counter cvProcessingFailureCounter;
    private final Timer cvProcessingTimer;
    private final Counter fileUploadCounter;
    private final Counter validationSuccessCounter;
    private final Counter validationFailureCounter;

    /**
     * Record CV processing request
     */
    public void recordCVProcessingRequest() {
        cvProcessingRequestsCounter.increment();
        log.debug("Recorded CV processing request");
    }

    /**
     * Record successful CV processing
     */
    public void recordCVProcessingSuccess() {
        cvProcessingSuccessCounter.increment();
        log.debug("Recorded CV processing success");
    }

    /**
     * Record failed CV processing
     */
    public void recordCVProcessingFailure() {
        cvProcessingFailureCounter.increment();
        log.debug("Recorded CV processing failure");
    }

    /**
     * Record CV processing duration
     * 
     * @param durationMs processing duration in milliseconds
     */
    public void recordCVProcessingDuration(long durationMs) {
        cvProcessingTimer.record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        log.debug("Recorded CV processing duration: {} ms", durationMs);
    }

    /**
     * Record file upload
     */
    public void recordFileUpload() {
        fileUploadCounter.increment();
        log.debug("Recorded file upload");
    }

    /**
     * Record successful validation
     */
    public void recordValidationSuccess() {
        validationSuccessCounter.increment();
        log.debug("Recorded validation success");
    }

    /**
     * Record failed validation
     */
    public void recordValidationFailure() {
        validationFailureCounter.increment();
        log.debug("Recorded validation failure");
    }

    /**
     * Get processing success rate
     * 
     * @return success rate as percentage
     */
    public double getProcessingSuccessRate() {
        double total = cvProcessingRequestsCounter.count();
        if (total == 0) {
            return 0.0;
        }
        return (cvProcessingSuccessCounter.count() / total) * 100.0;
    }

    /**
     * Get validation success rate
     * 
     * @return success rate as percentage
     */
    public double getValidationSuccessRate() {
        double total = validationSuccessCounter.count() + validationFailureCounter.count();
        if (total == 0) {
            return 0.0;
        }
        return (validationSuccessCounter.count() / total) * 100.0;
    }
}
