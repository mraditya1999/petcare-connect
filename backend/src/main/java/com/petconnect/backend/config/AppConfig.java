package com.petconnect.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    // Static Constants
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // Dynamic Properties
    public static String FRONTEND_URL;
    public static long JWT_EXPIRATION;

    @Value("${frontend-url}")
    public void setFrontendUrl(String frontendUrl) {
        FRONTEND_URL = frontendUrl;
    }

    @Value("${jwt.expiration}")
    public void setJwtExpiration(long jwtExpiration) {
        JWT_EXPIRATION = jwtExpiration;
    }
}
