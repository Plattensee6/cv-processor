package com.intuitech.cvprocessor.infrastructure.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for monitoring and metrics
 * 
 * Sets up Micrometer, Prometheus, and custom monitoring configurations.
 */
@Configuration
@Slf4j
public class MonitoringConfig {

    /**
     * Primary Prometheus meter registry
     */
    @Bean
    @Primary
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        log.info("Configuring Prometheus meter registry");
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    /**
     * Customize meter registry with common tags
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            String environment = getEnvironment();
            registry.config().commonTags(
                    "application", "cv-processor",
                    "version", "1.0.0",
                    "environment", environment
            );
            
            // Add meter filters
            registry.config().meterFilter(
                    MeterFilter.denyNameStartsWith("jvm.threads")
            );
            
            registry.config().meterFilter(
                    MeterFilter.denyNameStartsWith("system.cpu")
            );
            
            log.info("Meter registry customized with common tags");
        };
    }

    /**
     * Get current environment
     */
    @SuppressWarnings("unused")
    private String getEnvironment() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null || profile.isEmpty()) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        return profile != null ? profile : "dev";
    }
}
