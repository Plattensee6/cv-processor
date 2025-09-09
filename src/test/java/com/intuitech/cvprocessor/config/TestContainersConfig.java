package com.intuitech.cvprocessor.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import jakarta.annotation.PreDestroy;


/**
 * TestContainers configuration for integration tests
 * 
 * Provides PostgreSQL container for database integration tests.
 */
@TestConfiguration
@Profile("test")
public class TestContainersConfig {

    private static PostgreSQLContainer<?> postgresContainer;

    @Bean
    @Primary
    @SuppressWarnings("resource")
    public PostgreSQLContainer<?> postgresContainer() {
        if (postgresContainer == null) {
            postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                    .withDatabaseName("cvprocessor_test")
                    .withUsername("testuser")
                    .withPassword("testpass")
                    .withReuse(true);
            postgresContainer.start();
        }
        return postgresContainer;
    }

    @PreDestroy
    public void cleanup() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            postgresContainer.stop();
        }
    }


    @Bean
    @Primary
    public String testDatabaseUrl() {
        return postgresContainer.getJdbcUrl();
    }

    @Bean
    @Primary
    public String testDatabaseUsername() {
        return postgresContainer.getUsername();
    }

    @Bean
    @Primary
    public String testDatabasePassword() {
        return postgresContainer.getPassword();
    }
}

