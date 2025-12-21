package com.petconnect.backend.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties for security settings.
 * Maps from application.properties/yml using the "security.*" prefix.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security")
@Validated
public class SecurityProperties {

    @NotNull
    private List<String> authWhitelist;

    @NotNull
    private List<String> requestWhitelist;

    @NotNull
    private List<String> jwtExemptPaths;
}
