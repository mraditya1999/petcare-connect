package com.petconnect.backend.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    private final TwilioProperties twilioProperties;

    public TwilioConfig(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }

    @PostConstruct
    public void init() {
        Twilio.init(twilioProperties.accountSid(), twilioProperties.authToken());
    }
}

