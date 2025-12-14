package com.petconnect.backend.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Redis connection.
 * Maps from application.properties/yml spring.data.redis.* prefix.
 */
@ConfigurationProperties(prefix = "spring.data.redis")
@Validated
public record RedisProperties(
        @NotBlank String host,
        @Min(1) int port,
        String password,
        boolean sslEnabled,
        @Min(1) int timeout
) {
    public RedisProperties {
        if (password == null) {
            password = "";
        }
        if (timeout == 0) {
            timeout = 2000; // Default 2 seconds
        }
    }
}

