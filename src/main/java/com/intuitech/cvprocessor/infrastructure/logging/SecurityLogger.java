package com.intuitech.cvprocessor.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Security logging utility
 * 
 * Provides structured logging for security events and monitoring.
 */
@Component
@Slf4j
public class SecurityLogger {

    private static final String EVENT_TYPE_KEY = "eventType";
    private static final String USER_ID_KEY = "userId";
    private static final String IP_ADDRESS_KEY = "ipAddress";
    private static final String USER_AGENT_KEY = "userAgent";
    private static final String RESOURCE_KEY = "resource";
    private static final String ACTION_KEY = "action";
    private static final String STATUS_KEY = "status";

    /**
     * Log authentication attempt
     * 
     * @param userId the user ID
     * @param ipAddress the IP address
     * @param userAgent the user agent
     * @param success whether authentication was successful
     */
    public void logAuthenticationAttempt(String userId, String ipAddress, String userAgent, boolean success) {
        MDC.put(EVENT_TYPE_KEY, "AUTHENTICATION_ATTEMPT");
        MDC.put(USER_ID_KEY, userId);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put(USER_AGENT_KEY, userAgent);
        MDC.put(STATUS_KEY, success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("Authentication successful for user: {} from IP: {}", userId, ipAddress);
        } else {
            log.warn("Authentication failed for user: {} from IP: {}", userId, ipAddress);
        }
        
        clearMDC();
    }

    /**
     * Log authorization attempt
     * 
     * @param userId the user ID
     * @param resource the resource being accessed
     * @param action the action being performed
     * @param ipAddress the IP address
     * @param success whether authorization was successful
     */
    public void logAuthorizationAttempt(String userId, String resource, String action, 
                                      String ipAddress, boolean success) {
        MDC.put(EVENT_TYPE_KEY, "AUTHORIZATION_ATTEMPT");
        MDC.put(USER_ID_KEY, userId);
        MDC.put(RESOURCE_KEY, resource);
        MDC.put(ACTION_KEY, action);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put(STATUS_KEY, success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("Authorization successful for user: {} to access {} with action {}", 
                    userId, resource, action);
        } else {
            log.warn("Authorization failed for user: {} to access {} with action {}", 
                    userId, resource, action);
        }
        
        clearMDC();
    }

    /**
     * Log file upload attempt
     * 
     * @param userId the user ID
     * @param fileName the file name
     * @param fileSize the file size
     * @param ipAddress the IP address
     * @param success whether upload was successful
     */
    public void logFileUploadAttempt(String userId, String fileName, long fileSize, 
                                   String ipAddress, boolean success) {
        MDC.put(EVENT_TYPE_KEY, "FILE_UPLOAD_ATTEMPT");
        MDC.put(USER_ID_KEY, userId);
        MDC.put("fileName", fileName);
        MDC.put("fileSize", String.valueOf(fileSize));
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put(STATUS_KEY, success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("File upload successful for user: {} - file: {} ({} bytes)", 
                    userId, fileName, fileSize);
        } else {
            log.warn("File upload failed for user: {} - file: {} ({} bytes)", 
                    userId, fileName, fileSize);
        }
        
        clearMDC();
    }

    /**
     * Log suspicious activity
     * 
     * @param userId the user ID
     * @param activity the suspicious activity
     * @param ipAddress the IP address
     * @param details additional details
     */
    public void logSuspiciousActivity(String userId, String activity, String ipAddress, String details) {
        MDC.put(EVENT_TYPE_KEY, "SUSPICIOUS_ACTIVITY");
        MDC.put(USER_ID_KEY, userId);
        MDC.put("activity", activity);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put("details", details);
        MDC.put(STATUS_KEY, "DETECTED");
        
        log.error("Suspicious activity detected for user: {} - activity: {} from IP: {} - details: {}", 
                userId, activity, ipAddress, details);
        
        clearMDC();
    }

    /**
     * Log security violation
     * 
     * @param userId the user ID
     * @param violation the violation type
     * @param resource the resource involved
     * @param ipAddress the IP address
     * @param details additional details
     */
    public void logSecurityViolation(String userId, String violation, String resource, 
                                   String ipAddress, String details) {
        MDC.put(EVENT_TYPE_KEY, "SECURITY_VIOLATION");
        MDC.put(USER_ID_KEY, userId);
        MDC.put("violation", violation);
        MDC.put(RESOURCE_KEY, resource);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put("details", details);
        MDC.put(STATUS_KEY, "VIOLATION");
        
        log.error("Security violation detected for user: {} - violation: {} on resource: {} from IP: {} - details: {}", 
                userId, violation, resource, ipAddress, details);
        
        clearMDC();
    }

    /**
     * Log API access
     * 
     * @param userId the user ID
     * @param endpoint the API endpoint
     * @param method the HTTP method
     * @param ipAddress the IP address
     * @param userAgent the user agent
     * @param success whether access was successful
     */
    public void logApiAccess(String userId, String endpoint, String method, String ipAddress, 
                           String userAgent, boolean success) {
        MDC.put(EVENT_TYPE_KEY, "API_ACCESS");
        MDC.put(USER_ID_KEY, userId);
        MDC.put("endpoint", endpoint);
        MDC.put("method", method);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put(USER_AGENT_KEY, userAgent);
        MDC.put(STATUS_KEY, success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("API access successful for user: {} - {} {} from IP: {}", 
                    userId, method, endpoint, ipAddress);
        } else {
            log.warn("API access failed for user: {} - {} {} from IP: {}", 
                    userId, method, endpoint, ipAddress);
        }
        
        clearMDC();
    }

    /**
     * Log data access
     * 
     * @param userId the user ID
     * @param dataType the type of data accessed
     * @param action the action performed
     * @param ipAddress the IP address
     * @param success whether access was successful
     */
    public void logDataAccess(String userId, String dataType, String action, String ipAddress, boolean success) {
        MDC.put(EVENT_TYPE_KEY, "DATA_ACCESS");
        MDC.put(USER_ID_KEY, userId);
        MDC.put("dataType", dataType);
        MDC.put(ACTION_KEY, action);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put(STATUS_KEY, success ? "SUCCESS" : "FAILED");
        
        if (success) {
            log.info("Data access successful for user: {} - {} on {} from IP: {}", 
                    userId, action, dataType, ipAddress);
        } else {
            log.warn("Data access failed for user: {} - {} on {} from IP: {}", 
                    userId, action, dataType, ipAddress);
        }
        
        clearMDC();
    }

    /**
     * Log configuration change
     * 
     * @param userId the user ID
     * @param configKey the configuration key
     * @param oldValue the old value
     * @param newValue the new value
     * @param ipAddress the IP address
     */
    public void logConfigurationChange(String userId, String configKey, String oldValue, 
                                     String newValue, String ipAddress) {
        MDC.put(EVENT_TYPE_KEY, "CONFIGURATION_CHANGE");
        MDC.put(USER_ID_KEY, userId);
        MDC.put("configKey", configKey);
        MDC.put("oldValue", oldValue);
        MDC.put("newValue", newValue);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        MDC.put(STATUS_KEY, "CHANGED");
        
        log.info("Configuration changed by user: {} - key: {} from '{}' to '{}' from IP: {}", 
                userId, configKey, oldValue, newValue, ipAddress);
        
        clearMDC();
    }

    /**
     * Log system event
     * 
     * @param eventType the event type
     * @param eventDescription the event description
     * @param details additional details
     */
    public void logSystemEvent(String eventType, String eventDescription, Map<String, String> details) {
        MDC.put(EVENT_TYPE_KEY, "SYSTEM_EVENT");
        MDC.put("eventType", eventType);
        MDC.put("eventDescription", eventDescription);
        
        if (details != null) {
            details.forEach(MDC::put);
        }
        
        log.info("System event: {} - {}", eventType, eventDescription);
        
        clearMDC();
    }

    /**
     * Log security audit event
     * 
     * @param auditEvent the audit event
     * @param userId the user ID
     * @param ipAddress the IP address
     * @param details additional details
     */
    public void logSecurityAudit(String auditEvent, String userId, String ipAddress, Map<String, String> details) {
        MDC.put(EVENT_TYPE_KEY, "SECURITY_AUDIT");
        MDC.put("auditEvent", auditEvent);
        MDC.put(USER_ID_KEY, userId);
        MDC.put(IP_ADDRESS_KEY, ipAddress);
        
        if (details != null) {
            details.forEach(MDC::put);
        }
        
        log.info("Security audit: {} for user: {} from IP: {}", auditEvent, userId, ipAddress);
        
        clearMDC();
    }

    /**
     * Clear MDC context
     */
    private void clearMDC() {
        MDC.clear();
    }
}
