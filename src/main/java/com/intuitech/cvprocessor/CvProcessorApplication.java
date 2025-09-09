package com.intuitech.cvprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for CV Processor
 * 
 * This application processes CV documents using LLM technology to extract
 * specific fields and validate them according to business rules.
 */
@SpringBootApplication
@EnableScheduling
public class CvProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvProcessorApplication.class, args);
    }
}
