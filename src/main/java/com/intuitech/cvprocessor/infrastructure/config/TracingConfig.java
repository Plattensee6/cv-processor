package com.intuitech.cvprocessor.infrastructure.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Configuration for distributed tracing
 * 
 * Provides request correlation and performance tracking across the application.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "management.tracing.enabled", 
    havingValue = "true", 
    matchIfMissing = false
)
public class TracingConfig implements WebMvcConfigurer {

    private final Tracer tracer;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new TracingInterceptor(tracer));
    }

    /**
     * Custom tracing interceptor for request correlation
     */
    public static class TracingInterceptor implements org.springframework.web.servlet.HandlerInterceptor {
        
        private final Tracer tracer;
        
        public TracingInterceptor(Tracer tracer) {
            this.tracer = tracer;
        }
        
        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
            // Generate or extract correlation ID
            String correlationId = request.getHeader("X-Correlation-ID");
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }
            
            // Set correlation ID in response headers
            response.setHeader("X-Correlation-ID", correlationId);
            
            // Add to current span
            if (tracer.currentSpan() != null) {
                tracer.currentSpan().tag("correlation.id", correlationId);
                tracer.currentSpan().tag("request.method", request.getMethod());
                tracer.currentSpan().tag("request.uri", request.getRequestURI());
                tracer.currentSpan().tag("user.agent", request.getHeader("User-Agent"));
            }
            
            log.debug("Request correlation ID: {}", correlationId);
            return true;
        }
        
        @Override
        public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                  @NonNull Object handler, @Nullable Exception ex) {
            if (tracer.currentSpan() != null) {
                tracer.currentSpan().tag("response.status", String.valueOf(response.getStatus()));
                if (ex != null) {
                    tracer.currentSpan().tag("error", ex.getMessage());
                }
            }
        }
    }
}

/**
 * Service for manual span creation and tagging
 */
@Component
@RequiredArgsConstructor
@Slf4j
class TracingService {
    
    private final Tracer tracer;
    
    @NewSpan("cv-processing")
    public void traceCvProcessing(@SpanTag("requestId") Long requestId, 
                                 @SpanTag("fileName") String fileName) {
        log.debug("Tracing CV processing for request: {}, file: {}", requestId, fileName);
    }
    
    @NewSpan("field-extraction")
    public void traceFieldExtraction(@SpanTag("requestId") Long requestId,
                                   @SpanTag("model") String model) {
        log.debug("Tracing field extraction for request: {}, model: {}", requestId, model);
    }
    
    @NewSpan("validation")
    public void traceValidation(@SpanTag("requestId") Long requestId,
                              @SpanTag("validationType") String validationType) {
        log.debug("Tracing validation for request: {}, type: {}", requestId, validationType);
    }
    
    @NewSpan("llm-request")
    public void traceLlmRequest(@SpanTag("model") String model,
                              @SpanTag("promptLength") int promptLength) {
        log.debug("Tracing LLM request for model: {}, prompt length: {}", model, promptLength);
    }
    
    public void addCustomTag(String key, String value) {
        if (tracer.currentSpan() != null) {
            tracer.currentSpan().tag(key, value);
        }
    }
    
    public void recordError(String errorMessage, Throwable throwable) {
        if (tracer.currentSpan() != null) {
            tracer.currentSpan().tag("error", errorMessage);
            if (throwable != null) {
                tracer.currentSpan().tag("error.class", throwable.getClass().getSimpleName());
            }
        }
    }
}
