package com.intuitech.cvprocessor.techcore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Getter
@Setter
public class JwtConfigProperties {

    /**
     * Shared secret or private key material.
     */
    private String secret;

    /**
     * Access token validity in seconds.
     */
    private long accessTokenTtlSeconds = 1800; // 30 minutes

    /**
     * Issuer claim.
     */
    private String issuer = "cv-processor";

    /**
     * Audience claim.
     */
    private String audience = "cv-processor-admin";
}

