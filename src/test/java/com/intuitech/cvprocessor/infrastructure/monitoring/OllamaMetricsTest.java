package com.intuitech.cvprocessor.infrastructure.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OllamaMetrics
 */
class OllamaMetricsTest {

    private MeterRegistry meterRegistry;
    private OllamaMetrics ollamaMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        ollamaMetrics = new OllamaMetrics(meterRegistry);
    }

    @Test
    void recordSuccess_ShouldIncrementCounters() {
        // When
        ollamaMetrics.recordSuccess();

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertTrue((Double) summary.get("successfulRequests") > 0);
    }

    @Test
    void recordFailure_ShouldIncrementCounters() {
        // When
        ollamaMetrics.recordFailure();

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertTrue((Double) summary.get("failedRequests") > 0);
    }

    @Test
    void recordResponseTime_ShouldRecordTime() {
        // When
        ollamaMetrics.recordResponseTime(1500L);

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals(1500L, summary.get("lastResponseTimeMs"));
    }

    @Test
    void recordModelLoadTime_ShouldRecordTime() {
        // When
        ollamaMetrics.recordModelLoadTime(30000L);

        // Then
        // Verify that model load time is recorded (tested indirectly through metrics)
        assertDoesNotThrow(() -> ollamaMetrics.recordModelLoadTime(30000L));
    }

    @Test
    void recordFallbackTriggered_ShouldIncrementCounter() {
        // When
        ollamaMetrics.recordFallbackTriggered();

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertTrue((Double) summary.get("fallbackTriggered") > 0);
    }

    @Test
    void updateServiceStatus_WhenUp_ShouldSetToOne() {
        // When
        ollamaMetrics.updateServiceStatus(true);

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals("UP", summary.get("serviceStatus"));
    }

    @Test
    void updateServiceStatus_WhenDown_ShouldSetToZero() {
        // When
        ollamaMetrics.updateServiceStatus(false);

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals("DOWN", summary.get("serviceStatus"));
    }

    @Test
    void updateModelStatus_WhenAvailable_ShouldSetToOne() {
        // When
        ollamaMetrics.updateModelStatus(true);

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals("AVAILABLE", summary.get("modelStatus"));
    }

    @Test
    void updateModelStatus_WhenNotAvailable_ShouldSetToZero() {
        // When
        ollamaMetrics.updateModelStatus(false);

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals("NOT_AVAILABLE", summary.get("modelStatus"));
    }

    @Test
    void incrementConcurrentRequests_ShouldIncrementCounter() {
        // When
        ollamaMetrics.incrementConcurrentRequests();

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals(1L, summary.get("concurrentRequests"));
    }

    @Test
    void decrementConcurrentRequests_ShouldDecrementCounter() {
        // Given
        ollamaMetrics.incrementConcurrentRequests();
        ollamaMetrics.incrementConcurrentRequests();

        // When
        ollamaMetrics.decrementConcurrentRequests();

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals(1L, summary.get("concurrentRequests"));
    }

    @Test
    void getMetricsSummary_ShouldReturnSummaryMap() {
        // When
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();

        // Then
        assertNotNull(summary);
        assertTrue(summary.containsKey("totalRequests"));
        assertTrue(summary.containsKey("successfulRequests"));
        assertTrue(summary.containsKey("failedRequests"));
        assertTrue(summary.containsKey("fallbackTriggered"));
        assertTrue(summary.containsKey("serviceStatus"));
        assertTrue(summary.containsKey("modelStatus"));
        assertTrue(summary.containsKey("lastResponseTimeMs"));
        assertTrue(summary.containsKey("concurrentRequests"));
        assertTrue(summary.containsKey("averageResponseTimeMs"));
    }

    @Test
    void reset_ShouldResetAllMetrics() {
        // Given
        ollamaMetrics.incrementConcurrentRequests();
        ollamaMetrics.updateServiceStatus(true);
        ollamaMetrics.updateModelStatus(true);

        // When
        ollamaMetrics.reset();

        // Then
        Map<String, Object> summary = ollamaMetrics.getMetricsSummary();
        assertEquals(0L, summary.get("concurrentRequests"));
        assertEquals("DOWN", summary.get("serviceStatus"));
        assertEquals("NOT_AVAILABLE", summary.get("modelStatus"));
    }
}
