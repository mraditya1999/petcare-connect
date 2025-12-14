package com.petconnect.backend.utils;

/**
 * Application-wide constants.
 * Note: Security path configuration is in SecurityConfig via SecurityProperties.
 * These paths are used for request interceptors, not security rules.
 */
public final class AppConstants {
    /**
     * Paths that should be intercepted by request interceptors.
     * Note: Security rules are configured separately in SecurityConfig.
     */
    public static final String[] PROTECTED_PATHS = {
            "/auth/**",
            "/profiles/**",
            "/users/**",
            "/specialists/**",
            "/pets/**",
            "/forums/**",
            "/comments/**",
            "/likes/**",
            "/admin/**"
    };

    private AppConstants() {
        // Utility class - prevent instantiation
    }
}

