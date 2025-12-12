package com.petconnect.backend.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Cloudinary image storage service.
 * Maps from application.properties/yml cloudinary.* prefix.
 */
@ConfigurationProperties(prefix = "cloudinary")
@Validated
public record CloudinaryProperties(
        @NotBlank String cloudName,
        @NotBlank String apiKey,
        @NotBlank String apiSecret
) { }

