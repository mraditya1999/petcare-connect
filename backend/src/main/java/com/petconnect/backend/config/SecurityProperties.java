package com.petconnect.backend.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties for security settings.
 * Maps from application.properties/yml security.* prefix.
 */
@ConfigurationProperties(prefix = "security")
@Validated
public record SecurityProperties(
        @NotNull List<String> authWhitelist,
        @NotNull List<String> getRequestWhitelist,
        @NotNull List<String> jwtExemptPaths
) {}

