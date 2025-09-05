package com.intuitech.cvprocessor.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application metrics
 * 
 * Sets up Micrometer metrics for monitoring and observability.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MetricsConfig {

    private final MeterRegistry meterRegistry;

    /**
     * Create counter for CV processing requests
     */
    @Bean
    public Counter cvProcessingRequestsCounter() {
        log.info("Creating CV processing requests counter");
        return Counter.builder("cv.processing.requests.total")
                .description("Total number of CV processing requests")
                .register(meterRegistry);
    }

    /**
     * Create counter for successful CV processing
     */
    @Bean
    public Counter cvProcessingSuccessCounter() {
        log.info("Creating CV processing success counter");
        return Counter.builder("cv.processing.success.total")
                .description("Total number of successful CV processing requests")
                .register(meterRegistry);
    }

    /**
     * Create counter for failed CV processing
     */
    @Bean
    public Counter cvProcessingFailureCounter() {
        log.info("Creating CV processing failure counter");
        return Counter.builder("cv.processing.failure.total")
                .description("Total number of failed CV processing requests")
                .register(meterRegistry);
    }

    /**
     * Create timer for CV processing duration
     */
    @Bean
    public Timer cvProcessingTimer() {
        log.info("Creating CV processing timer");
        return Timer.builder("cv.processing.duration")
                .description("CV processing duration")
                .register(meterRegistry);
    }

    /**
     * Create counter for file uploads
     */
    @Bean
    public Counter fileUploadCounter() {
        log.info("Creating file upload counter");
        return Counter.builder("cv.file.upload.total")
                .description("Total number of file uploads")
                .register(meterRegistry);
    }

    /**
     * Create counter for validation results
     */
    @Bean
    public Counter validationSuccessCounter() {
        log.info("Creating validation success counter");
        return Counter.builder("cv.validation.success.total")
                .description("Total number of successful validations")
                .register(meterRegistry);
    }

    /**
     * Create counter for validation failures
     */
    @Bean
    public Counter validationFailureCounter() {
        log.info("Creating validation failure counter");
        return Counter.builder("cv.validation.failure.total")
                .description("Total number of failed validations")
                .register(meterRegistry);
    }
}
