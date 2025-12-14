package com.petconnect.backend.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties for CORS settings.
 * Maps from application.properties/yml cors.* prefix.
 */
@ConfigurationProperties(prefix = "cors")
@Validated
public record CorsProperties(
        @NotEmpty List<String> allowedOrigins,
        boolean allowCredentials,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        List<String> exposedHeaders
) {
    public CorsProperties {
        if (allowedMethods == null) {
            allowedMethods = List.of("*");
        }
        if (allowedHeaders == null) {
            allowedHeaders = List.of("*");
        }
        if (exposedHeaders == null) {
            exposedHeaders = List.of("Authorization");
        }
        if (!allowCredentials) {
            allowCredentials = true; // Default to true
        }
    }
}

