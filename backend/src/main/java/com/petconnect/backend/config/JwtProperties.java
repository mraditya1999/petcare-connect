package com.petconnect.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
@Validated
public class JwtProperties {

    @NotEmpty
    private String secret;

    @Positive
    private long expiration;
}
