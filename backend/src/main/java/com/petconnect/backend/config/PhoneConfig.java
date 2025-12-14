package com.petconnect.backend.config;

import com.petconnect.backend.utils.PhoneUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to initialize PhoneUtils with configured country code.
 */
@Configuration
@EnableConfigurationProperties(PhoneProperties.class)
public class PhoneConfig {

    private final PhoneProperties phoneProperties;

    public PhoneConfig(PhoneProperties phoneProperties) {
        this.phoneProperties = phoneProperties;
    }

    @PostConstruct
    public void init() {
        PhoneUtils.setDefaultCountryCode(phoneProperties.defaultCountryCode());
    }
}

