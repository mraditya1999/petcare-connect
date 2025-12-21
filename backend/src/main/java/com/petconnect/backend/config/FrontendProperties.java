package com.petconnect.backend.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties for frontend URLs and CORS settings.
 * Maps from application.properties/yml frontend.* prefix.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "frontend")
@Validated
public class FrontendProperties {

    @NotEmpty
    private List<String> urls;
}
