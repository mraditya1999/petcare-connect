    package com.petconnect.backend.config;

    import jakarta.validation.constraints.NotBlank;
    import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.validation.annotation.Validated;

    /**
     * Configuration properties for Twilio SMS service.
     * Maps from application.properties/yml twilio.* prefix.
     */
    @ConfigurationProperties(prefix = "twilio")
    @Validated
    public record TwilioProperties(
            @NotBlank String accountSid,
            @NotBlank String authToken,
            @NotBlank String fromPhone
    ) {}

