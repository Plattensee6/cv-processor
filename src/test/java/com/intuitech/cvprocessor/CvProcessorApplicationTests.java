package com.intuitech.cvprocessor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic application context test
 * 
 * Verifies that the Spring Boot application starts successfully
 * and all beans are properly configured.
 */
@SpringBootTest
@ActiveProfiles("test")
class CvProcessorApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // If this test passes, it means all beans are properly configured
    }
}
