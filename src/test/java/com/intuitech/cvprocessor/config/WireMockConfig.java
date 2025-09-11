package com.intuitech.cvprocessor.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * WireMock configuration for mocking external services
 * 
 * Provides WireMock server for mocking Ollama API.
 */
@TestConfiguration
@Profile("test")
public class WireMockConfig {

    @Bean
    @Primary
    public WireMockServer ollamaWireMockServer() {
        WireMockServer server = new WireMockServer(
                WireMockConfiguration.options()
                        .port(11435) // Different port to avoid conflicts
                        .extensions("com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer")
        );
        server.start();
        return server;
    }

}
