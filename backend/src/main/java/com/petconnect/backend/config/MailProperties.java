package com.petconnect.backend.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for SMTP/Mail service.
 * Maps from application.properties/yml spring.mail.* prefix.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.mail")
@Validated
public class MailProperties {

    @NotBlank
    private String host;

    @Positive
    private int port;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private boolean debug = false;

    private String sslTrust = "smtp.gmail.com";

    private boolean starttlsRequired = true;
}
