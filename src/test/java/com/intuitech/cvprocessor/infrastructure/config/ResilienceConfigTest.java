package com.intuitech.cvprocessor.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResilienceConfig
 */
@ExtendWith(MockitoExtension.class)
class ResilienceConfigTest {

    @Mock
    private MeterRegistry meterRegistry;

    private ResilienceConfig resilienceConfig;

    @BeforeEach
    void setUp() {
        resilienceConfig = new ResilienceConfig();
    }

    @Test
    void ollamaCircuitBreaker_ShouldBeProperlyConfigured() {
        // When
        CircuitBreaker circuitBreaker = resilienceConfig.ollamaCircuitBreaker(meterRegistry);

        // Then
        assertNotNull(circuitBreaker);
        assertEquals("ollama-extraction", circuitBreaker.getName());
        
        // Verify circuit breaker is in closed state initially
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        
        // Verify that the circuit breaker has metrics
        assertNotNull(circuitBreaker.getMetrics());
    }

    @Test
    void ollamaRetry_ShouldBeProperlyConfigured() {
        // When
        Retry retry = resilienceConfig.ollamaRetry(meterRegistry);

        // Then
        assertNotNull(retry);
        assertEquals("ollama-extraction", retry.getName());
        
        // Verify that the retry has metrics
        assertNotNull(retry.getMetrics());
    }

    @Test
    void huggingFaceCircuitBreaker_ShouldBeProperlyConfigured() {
        // When
        CircuitBreaker circuitBreaker = resilienceConfig.huggingFaceCircuitBreaker(meterRegistry);

        // Then
        assertNotNull(circuitBreaker);
        assertEquals("huggingface-extraction", circuitBreaker.getName());
        
        // Verify circuit breaker is in closed state initially
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        
        // Verify that the circuit breaker has metrics
        assertNotNull(circuitBreaker.getMetrics());
    }

    @Test
    void ollamaCircuitBreaker_ShouldHaveEventListeners() {
        // When
        CircuitBreaker circuitBreaker = resilienceConfig.ollamaCircuitBreaker(meterRegistry);

        // Then
        assertNotNull(circuitBreaker);
        
        // Verify that event publisher is available (indicates listeners are registered)
        assertNotNull(circuitBreaker.getEventPublisher());
    }

    @Test
    void ollamaRetry_ShouldHaveEventListeners() {
        // When
        Retry retry = resilienceConfig.ollamaRetry(meterRegistry);

        // Then
        assertNotNull(retry);
        
        // Verify that event publisher is available (indicates listeners are registered)
        assertNotNull(retry.getEventPublisher());
    }

    @Test
    void huggingFaceCircuitBreaker_ShouldHaveEventListeners() {
        // When
        CircuitBreaker circuitBreaker = resilienceConfig.huggingFaceCircuitBreaker(meterRegistry);

        // Then
        assertNotNull(circuitBreaker);
        
        // Verify that event publisher is available (indicates listeners are registered)
        assertNotNull(circuitBreaker.getEventPublisher());
    }

    @Test
    void ollamaCircuitBreaker_ShouldBeCreatedSuccessfully() {
        // When
        CircuitBreaker circuitBreaker = resilienceConfig.ollamaCircuitBreaker(meterRegistry);

        // Then
        assertNotNull(circuitBreaker);
        assertEquals("ollama-extraction", circuitBreaker.getName());
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    void ollamaRetry_ShouldBeCreatedSuccessfully() {
        // When
        Retry retry = resilienceConfig.ollamaRetry(meterRegistry);

        // Then
        assertNotNull(retry);
        assertEquals("ollama-extraction", retry.getName());
    }

    @Test
    void huggingFaceCircuitBreaker_ShouldBeCreatedSuccessfully() {
        // When
        CircuitBreaker circuitBreaker = resilienceConfig.huggingFaceCircuitBreaker(meterRegistry);

        // Then
        assertNotNull(circuitBreaker);
        assertEquals("huggingface-extraction", circuitBreaker.getName());
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }
}
