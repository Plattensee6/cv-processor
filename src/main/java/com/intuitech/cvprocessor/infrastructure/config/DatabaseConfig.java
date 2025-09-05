package com.intuitech.cvprocessor.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Database configuration and initialization
 * 
 * Handles database connection setup and provides initialization logic.
 */
@Configuration
@Slf4j
public class DatabaseConfig {

    /**
     * Database connection test runner
     * 
     * Tests database connectivity on application startup.
     * Only runs in development profile.
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner databaseConnectionTest(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                log.info("✅ Database connection successful!");
                log.info("Database URL: {}", connection.getMetaData().getURL());
                log.info("Database Product: {}", connection.getMetaData().getDatabaseProductName());
                log.info("Database Version: {}", connection.getMetaData().getDatabaseProductVersion());
            } catch (Exception e) {
                log.error("❌ Database connection failed: {}", e.getMessage());
                throw new RuntimeException("Failed to connect to database", e);
            }
        };
    }
}
