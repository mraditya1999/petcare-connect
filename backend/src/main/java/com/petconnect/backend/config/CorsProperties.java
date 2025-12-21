package com.petconnect.backend.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties for CORS settings.
 * Maps from application.properties/yml cors.* prefix.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
@Validated
public class CorsProperties {

    @NotEmpty
    private List<String> allowedOrigins;

    private boolean allowCredentials;

    private List<String> allowedMethods;

    private List<String> allowedHeaders;

    private List<String> exposedHeaders;

    public CorsProperties() {
        if (allowedMethods == null) {
            allowedMethods = List.of("*");
        }
        if (allowedHeaders == null) {
            allowedHeaders = List.of("*");
        }
        if (exposedHeaders == null) {
            exposedHeaders = List.of("Authorization");
        }
    }
}