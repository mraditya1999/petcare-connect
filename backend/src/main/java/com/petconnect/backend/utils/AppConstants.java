package com.petconnect.backend.utils;

public final class AppConstants {
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

    private AppConstants() { }
}

