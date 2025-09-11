package com.intuitech.cvprocessor.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j configuration for circuit breaker and retry patterns
 * 
 * Provides resilience patterns for external service calls, particularly Ollama integration.
 */
@Configuration
@Slf4j
public class ResilienceConfig {

    /**
     * Circuit breaker configuration for Ollama extraction
     */
    @Bean
    public CircuitBreaker ollamaCircuitBreaker(MeterRegistry meterRegistry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 50% failure rate threshold
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait 30s before trying again
                .slidingWindowSize(10) // Last 10 calls
                .minimumNumberOfCalls(5) // Minimum 5 calls before calculating failure rate
                .permittedNumberOfCallsInHalfOpenState(3) // Allow 3 calls in half-open state
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .recordExceptions(
                    java.net.ConnectException.class,
                    java.net.SocketTimeoutException.class,
                    org.springframework.web.client.ResourceAccessException.class,
                    org.springframework.web.client.HttpClientErrorException.class,
                    org.springframework.web.client.HttpServerErrorException.class
                )
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker.of("ollama-extraction", config);
        
        // Add event listeners for monitoring
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    log.info("Circuit breaker state transition: {} -> {}", 
                            event.getStateTransition().getFromState(), 
                            event.getStateTransition().getToState());
                })
                .onFailureRateExceeded(event -> {
                    log.warn("Circuit breaker failure rate exceeded: {}%", 
                            event.getFailureRate());
                })
                .onCallNotPermitted(event -> {
                    log.warn("Circuit breaker call not permitted: {}", 
                            event.getEventType());
                });

        // Register metrics
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    meterRegistry.gauge("resilience4j.circuitbreaker.state", 
                            circuitBreaker.getState().ordinal());
                });

        return circuitBreaker;
    }

    /**
     * Retry configuration for Ollama extraction
     */
    @Bean
    public Retry ollamaRetry(MeterRegistry meterRegistry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3) // Maximum 3 attempts
                .waitDuration(Duration.ofSeconds(2)) // Wait 2s between retries
                .retryExceptions(
                    java.net.ConnectException.class,
                    java.net.SocketTimeoutException.class,
                    org.springframework.web.client.ResourceAccessException.class,
                    org.springframework.web.client.HttpServerErrorException.class
                )
                .ignoreExceptions(
                    org.springframework.web.client.HttpClientErrorException.class,
                    IllegalArgumentException.class
                )
                .build();

        Retry retry = Retry.of("ollama-extraction", config);
        
        // Add event listeners for monitoring
        retry.getEventPublisher()
                .onRetry(event -> {
                    log.info("Retry attempt {} for {}", 
                            event.getNumberOfRetryAttempts(), 
                            event.getName());
                })
                .onError(event -> {
                    log.warn("Retry failed after {} attempts: {}", 
                            event.getNumberOfRetryAttempts(), 
                            event.getLastThrowable().getMessage());
                })
                .onSuccess(event -> {
                    log.info("Retry succeeded after {} attempts", 
                            event.getNumberOfRetryAttempts());
                });

        return retry;
    }

}
