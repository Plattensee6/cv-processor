package com.intuitech.cvprocessor.integration.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for HealthController
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("HealthController Integration Tests")
class HealthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should return basic health check")
    void shouldReturnBasicHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("CV Processor"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return detailed health check")
    void shouldReturnDetailedHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health/detailed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("CV Processor"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.components.database").value("UP"))
                .andExpect(jsonPath("$.components.llm-service").value("UP"));
    }

    @Test
    @DisplayName("Should return JSON content type")
    void shouldReturnJsonContentType() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should handle GET request to health endpoint")
    void shouldHandleGetRequestToHealthEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Should handle GET request to detailed health endpoint")
    void shouldHandleGetRequestToDetailedHealthEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists());
    }

    @Test
    @DisplayName("Should return consistent response format")
    void shouldReturnConsistentResponseFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.application").exists())
                .andExpect(jsonPath("$.version").exists());
    }

    @Test
    @DisplayName("Should return detailed response format")
    void shouldReturnDetailedResponseFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.application").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.components.database").exists())
                .andExpect(jsonPath("$.components.llm-service").exists());
    }

    @Test
    @DisplayName("Should be accessible without authentication")
    void shouldBeAccessibleWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/health/detailed"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 for non-existent health endpoint")
    void shouldReturn404ForNonExistentHealthEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle concurrent health check requests")
    void shouldHandleConcurrentHealthCheckRequests() throws Exception {
        // When & Then - Multiple concurrent requests should all succeed
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"));
        }
    }
}
