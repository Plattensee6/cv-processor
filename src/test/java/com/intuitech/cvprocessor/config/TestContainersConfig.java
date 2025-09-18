package com.intuitech.cvprocessor.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

/**
 * TestContainers configuration for integration tests
 * 
 * Provides PostgreSQL container for database integration tests.
 */
@TestConfiguration
@Profile("test")
public class TestContainersConfig {

    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("cvprocessor_test")
                .withUsername("testuser")
                .withPassword("testpass")
                .withReuse(true);
        postgresContainer.start();
    }

    @Bean
    @Primary
    public PostgreSQLContainer<?> postgresContainer() {
        return postgresContainer;
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
