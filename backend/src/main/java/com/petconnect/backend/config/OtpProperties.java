package com.petconnect.backend.config;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "otp")
@Validated
public class OtpProperties {

    @Positive
    private int length = 6;

    @Positive
    private int ttlMinutes = 5;

    @Positive
    private int maxVerifyAttempts = 5;

    @Positive
    private int resendCooldownSeconds = 30;

    @Positive
    private long blockSeconds = 3600L;

    @Positive
    private long verificationTokenTtlHours = 24L;
}

