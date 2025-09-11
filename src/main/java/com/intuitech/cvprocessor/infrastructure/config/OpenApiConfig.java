package com.intuitech.cvprocessor.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for CV Processor API
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CV Processor API")
                        .description("""
                                CV Processing Application with LLM Integration
                                
                                This API provides endpoints for:
                                - Uploading and processing CV documents (PDF, DOC, DOCX)
                                - Extracting specific fields using Ollama LLM
                                - Validating extracted data according to business rules
                                - Monitoring processing status and health
                                
                                ## Supported File Formats
                                - PDF documents
                                - Microsoft Word documents (.doc, .docx)
                                
                                ## Field Extraction
                                The system extracts the following fields from CVs:
                                - **Work Experience**: Years of experience and details
                                - **Skills**: List of technical and soft skills
                                - **Languages**: Language proficiencies
                                - **Profile**: Professional summary and interests
                                
                                ## Validation Rules
                                - Work Experience: 0-2 years required
                                - Skills: Must include Java and LLM/AI
                                - Languages: Must include Hungarian and English
                                - Profile: Must include GenAI and Java interest
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IntuiTech")
                                .email("info@intuitech.com")
                                .url("https://intuitech.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.intuitech.com")
                                .description("Production Server")
                ));
    }
}
