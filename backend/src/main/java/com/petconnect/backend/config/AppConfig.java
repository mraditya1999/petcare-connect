package com.petconnect.backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        CloudinaryProperties.class,
        CorsProperties.class,
        FrontendProperties.class,
        GitHubProperties.class,
        JwtProperties.class,
        MailProperties.class,
        OtpProperties.class,
        RedisProperties.class,
        SecurityProperties.class,
        TwilioProperties.class})
public class AppConfig {
}
