package com.petconnect.backend.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for SMTP/Mail service.
 * Maps from application.properties/yml spring.mail.* prefix.
 */
@ConfigurationProperties(prefix = "spring.mail")
@Validated
public record MailProperties(
        @NotBlank String host,
        @Positive int port,
        @NotBlank String username,
        @NotBlank String password
) { }
