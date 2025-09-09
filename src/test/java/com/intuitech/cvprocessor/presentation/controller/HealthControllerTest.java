package com.intuitech.cvprocessor.presentation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HealthController
 * 
 * Tests the health check endpoints to ensure they return proper responses.
 */
@WebMvcTest(controllers = HealthController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void health_ShouldReturnOkStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("CV Processor"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void detailedHealth_ShouldReturnDetailedStatus() throws Exception {
        mockMvc.perform(get("/api/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("CV Processor"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.components.database").value("UP"))
                .andExpect(jsonPath("$.components.llm-service").value("UP"));
    }
}
