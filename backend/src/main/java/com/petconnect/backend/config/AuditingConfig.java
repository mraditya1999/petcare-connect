package com.petconnect.backend.config;

import com.petconnect.backend.security.SecurityAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    public SecurityAuditorAware auditorProvider() {
        return new SecurityAuditorAware();
    }
}
