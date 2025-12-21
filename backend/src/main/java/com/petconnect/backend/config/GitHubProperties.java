package com.petconnect.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "github")
@Validated
@Getter
@Setter
public class GitHubProperties {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String redirectUri;

    private String apiBaseUrl = "https://api.github.com";

    private String[] scopes = new String[]{"user:email"};
}
