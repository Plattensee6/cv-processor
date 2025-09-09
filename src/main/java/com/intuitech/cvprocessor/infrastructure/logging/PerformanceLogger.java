package com.intuitech.cvprocessor.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Performance logging utility
 * 
 * Provides structured logging for performance monitoring and analysis.
 */
@Component
@Slf4j
public class PerformanceLogger {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String OPERATION_KEY = "operation";
    private static final String DURATION_KEY = "duration";
    private static final String STATUS_KEY = "status";

    /**
     * Log the start of an operation
     * 
     * @param operation the operation name
     * @param requestId the request ID
     * @return the start time for duration calculation
     */
    public LocalDateTime logOperationStart(String operation, String requestId) {
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(OPERATION_KEY, operation);
        MDC.put(STATUS_KEY, "STARTED");
        
        log.info("Operation started: {}", operation);
        
        return LocalDateTime.now();
    }

    /**
     * Log the completion of an operation
     * 
     * @param operation the operation name
     * @param requestId the request ID
     * @param startTime the start time
     * @param success whether the operation was successful
     */
    public void logOperationEnd(String operation, String requestId, LocalDateTime startTime, boolean success) {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(OPERATION_KEY, operation);
        MDC.put(DURATION_KEY, String.valueOf(duration.toMillis()));
        MDC.put(STATUS_KEY, success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("Operation completed successfully: {} in {}ms", operation, duration.toMillis());
        } else {
            log.warn("Operation failed: {} in {}ms", operation, duration.toMillis());
        }
        
        clearMDC();
    }

    /**
     * Log the completion of an operation with custom message
     * 
     * @param operation the operation name
     * @param requestId the request ID
     * @param startTime the start time
     * @param success whether the operation was successful
     * @param message custom message
     */
    public void logOperationEnd(String operation, String requestId, LocalDateTime startTime, 
                               boolean success, String message) {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(OPERATION_KEY, operation);
        MDC.put(DURATION_KEY, String.valueOf(duration.toMillis()));
        MDC.put(STATUS_KEY, success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("Operation completed successfully: {} in {}ms - {}", operation, duration.toMillis(), message);
        } else {
            log.warn("Operation failed: {} in {}ms - {}", operation, duration.toMillis(), message);
        }
        
        clearMDC();
    }

    /**
     * Log a performance metric
     * 
     * @param metricName the metric name
     * @param value the metric value
     * @param unit the unit of measurement
     */
    public void logMetric(String metricName, double value, String unit) {
        MDC.put("metricName", metricName);
        MDC.put("metricValue", String.valueOf(value));
        MDC.put("metricUnit", unit);
        
        log.info("Performance metric: {} = {} {}", metricName, value, unit);
        
        clearMDC();
    }

    /**
     * Log a performance metric with request context
     * 
     * @param metricName the metric name
     * @param value the metric value
     * @param unit the unit of measurement
     * @param requestId the request ID
     */
    public void logMetric(String metricName, double value, String unit, String requestId) {
        MDC.put("metricName", metricName);
        MDC.put("metricValue", String.valueOf(value));
        MDC.put("metricUnit", unit);
        MDC.put(REQUEST_ID_KEY, requestId);
        
        log.info("Performance metric: {} = {} {} for request {}", metricName, value, unit, requestId);
        
        clearMDC();
    }

    /**
     * Log database query performance
     * 
     * @param queryName the query name
     * @param duration the query duration
     * @param rowCount the number of rows affected
     */
    public void logDatabaseQuery(String queryName, Duration duration, int rowCount) {
        MDC.put("queryName", queryName);
        MDC.put("queryDuration", String.valueOf(duration.toMillis()));
        MDC.put("rowCount", String.valueOf(rowCount));
        
        log.info("Database query: {} executed in {}ms, {} rows affected", 
                queryName, duration.toMillis(), rowCount);
        
        clearMDC();
    }

    /**
     * Log external service call performance
     * 
     * @param serviceName the service name
     * @param endpoint the endpoint
     * @param duration the call duration
     * @param success whether the call was successful
     */
    public void logExternalServiceCall(String serviceName, String endpoint, Duration duration, boolean success) {
        MDC.put("serviceName", serviceName);
        MDC.put("endpoint", endpoint);
        MDC.put("callDuration", String.valueOf(duration.toMillis()));
        MDC.put("callStatus", success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("External service call: {} to {} completed in {}ms", 
                    serviceName, endpoint, duration.toMillis());
        } else {
            log.warn("External service call: {} to {} failed in {}ms", 
                    serviceName, endpoint, duration.toMillis());
        }
        
        clearMDC();
    }

    /**
     * Log file processing performance
     * 
     * @param fileName the file name
     * @param fileSize the file size in bytes
     * @param duration the processing duration
     * @param success whether processing was successful
     */
    public void logFileProcessing(String fileName, long fileSize, Duration duration, boolean success) {
        MDC.put("fileName", fileName);
        MDC.put("fileSize", String.valueOf(fileSize));
        MDC.put("processingDuration", String.valueOf(duration.toMillis()));
        MDC.put("processingStatus", success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("File processing: {} ({} bytes) completed in {}ms", 
                    fileName, fileSize, duration.toMillis());
        } else {
            log.warn("File processing: {} ({} bytes) failed in {}ms", 
                    fileName, fileSize, duration.toMillis());
        }
        
        clearMDC();
    }

    /**
     * Generate a unique request ID
     * 
     * @return a unique request ID
     */
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Clear MDC context
     */
    private void clearMDC() {
        MDC.clear();
    }
}
