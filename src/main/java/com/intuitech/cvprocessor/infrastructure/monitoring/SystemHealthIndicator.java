package com.intuitech.cvprocessor.infrastructure.monitoring;

import com.intuitech.cvprocessor.infrastructure.service.OllamaHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive system health indicator
 * 
 * Provides detailed health information about all system components.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SystemHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final OllamaHealthService ollamaHealthService;
    private final CustomMetrics customMetrics;

    @Override
    public Health health() {
        log.debug("Performing comprehensive system health check");
        
        Map<String, Object> healthDetails = new HashMap<>();
        boolean overallHealthy = true;
        
        // Database Health Check
        Map<String, Object> databaseHealth = checkDatabaseHealth();
        healthDetails.put("database", databaseHealth);
        if (!(Boolean) databaseHealth.get("healthy")) {
            overallHealthy = false;
        }
        
        // Ollama Health Check
        Map<String, Object> ollamaHealth = ollamaHealthService.getHealthInfo();
        healthDetails.put("ollama", ollamaHealth);
        if (!"UP".equals(ollamaHealth.get("status"))) {
            overallHealthy = false;
        }
        
        // Application Metrics
        Map<String, Object> metrics = customMetrics.getMetricsSummary();
        healthDetails.put("metrics", metrics);
        
        // System Information
        Map<String, Object> systemInfo = getSystemInfo();
        healthDetails.put("system", systemInfo);
        
        // Overall Status
        healthDetails.put("overallStatus", overallHealthy ? "UP" : "DOWN");
        healthDetails.put("timestamp", LocalDateTime.now());
        
        Health.Builder healthBuilder = overallHealthy ? Health.up() : Health.down();
        
        // Add all health details
        healthDetails.forEach(healthBuilder::withDetail);
        
        if (overallHealthy) {
            log.debug("System health check completed successfully");
        } else {
            log.warn("System health check completed with issues");
        }
        
        return healthBuilder.build();
    }
    
    private Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5); // 5 second timeout
                long responseTime = System.currentTimeMillis() - startTime;
                
                dbHealth.put("healthy", isValid);
                dbHealth.put("status", isValid ? "UP" : "DOWN");
                dbHealth.put("responseTimeMs", responseTime);
                dbHealth.put("connectionValid", isValid);
                
                if (isValid) {
                    // Get additional database info
                    String url = connection.getMetaData().getURL();
                    String driverName = connection.getMetaData().getDriverName();
                    String driverVersion = connection.getMetaData().getDriverVersion();
                    
                    dbHealth.put("url", url);
                    dbHealth.put("driverName", driverName);
                    dbHealth.put("driverVersion", driverVersion);
                }
                
                log.debug("Database health check completed: {} ({}ms)", 
                         isValid ? "UP" : "DOWN", responseTime);
                
            }
        } catch (Exception e) {
            log.error("Database health check failed: {}", e.getMessage());
            dbHealth.put("healthy", false);
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
        }
        
        return dbHealth;
    }
    
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        try {
            Runtime runtime = Runtime.getRuntime();
            
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            systemInfo.put("totalMemoryMB", totalMemory / (1024 * 1024));
            systemInfo.put("freeMemoryMB", freeMemory / (1024 * 1024));
            systemInfo.put("usedMemoryMB", usedMemory / (1024 * 1024));
            systemInfo.put("maxMemoryMB", maxMemory / (1024 * 1024));
            systemInfo.put("memoryUsagePercent", (double) usedMemory / maxMemory * 100);
            
            systemInfo.put("availableProcessors", runtime.availableProcessors());
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            
            // JVM uptime
            long uptime = System.currentTimeMillis() - 
                         java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
            systemInfo.put("uptimeMs", uptime);
            systemInfo.put("uptimeHours", uptime / (1000 * 60 * 60));
            
        } catch (Exception e) {
            log.error("Failed to get system information: {}", e.getMessage());
            systemInfo.put("error", e.getMessage());
        }
        
        return systemInfo;
    }
}
