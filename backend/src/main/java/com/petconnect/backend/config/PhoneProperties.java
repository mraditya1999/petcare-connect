package com.petconnect.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for phone number normalization.
 * Maps from application.properties/yml phone.* prefix.
 */
@ConfigurationProperties(prefix = "phone")
@Validated
public record PhoneProperties(
        String defaultCountryCode
) {
    public PhoneProperties {
        if (defaultCountryCode == null || defaultCountryCode.isBlank()) {
            defaultCountryCode = "+91"; // Default to India
        }
    }
}

