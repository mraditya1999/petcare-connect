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

    private boolean allowCredentials = true;

    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    private List<String> allowedHeaders = List.of("*");

    private List<String> exposedHeaders = List.of("Authorization");
}