//package com.petconnect.backend.utils;
//
//import org.springframework.core.env.Environment;
//
///**
// * Utility class for configuration operations.
// * Provides methods to safely access configuration properties.
// */
//public final class ConfigUtils {
//
//    private ConfigUtils() {
//        // Utility class
//    }
//
//    /**
//     * Get required string property.
//     */
//    public static String getRequiredString(Environment env, String key) {
//        String value = env.getProperty(key);
//        if (value == null || value.trim().isEmpty()) {
//            throw new IllegalStateException("Required configuration property '" + key + "' is missing or empty");
//        }
//        return value.trim();
//    }
//
//    /**
//     * Get string property with default value.
//     */
//    public static String getString(Environment env, String key, String defaultValue) {
//        String value = env.getProperty(key);
//        return value != null ? value.trim() : defaultValue;
//    }
//
//    /**
//     * Get required integer property.
//     */
//    public static int getRequiredInt(Environment env, String key) {
//        String value = getRequiredString(env, key);
//        try {
//            return Integer.parseInt(value);
//        } catch (NumberFormatException e) {
//            throw new IllegalStateException("Configuration property '" + key + "' must be a valid integer: " + value);
//        }
//    }
//
//    /**
//     * Get integer property with default value.
//     */
//    public static int getInt(Environment env, String key, int defaultValue) {
//        String value = env.getProperty(key);
//        if (value == null || value.trim().isEmpty()) {
//            return defaultValue;
//        }
//        try {
//            return Integer.parseInt(value.trim());
//        } catch (NumberFormatException e) {
//            throw new IllegalStateException("Configuration property '" + key + "' must be a valid integer: " + value);
//        }
//    }
//
//    /**
//     * Get required long property.
//     */
//    public static long getRequiredLong(Environment env, String key) {
//        String value = getRequiredString(env, key);
//        try {
//            return Long.parseLong(value);
//        } catch (NumberFormatException e) {
//            throw new IllegalStateException("Configuration property '" + key + "' must be a valid long: " + value);
//        }
//    }
//
//    /**
//     * Get long property with default value.
//     */
//    public static long getLong(Environment env, String key, long defaultValue) {
//        String value = env.getProperty(key);
//        if (value == null || value.trim().isEmpty()) {
//            return defaultValue;
//        }
//        try {
//            return Long.parseLong(value.trim());
//        } catch (NumberFormatException e) {
//            throw new IllegalStateException("Configuration property '" + key + "' must be a valid long: " + value);
//        }
//    }
//
//    /**
//     * Get required boolean property.
//     */
//    public static boolean getRequiredBoolean(Environment env, String key) {
//        String value = getRequiredString(env, key);
//        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
//            return Boolean.parseBoolean(value);
//        }
//        throw new IllegalStateException("Configuration property '" + key + "' must be 'true' or 'false': " + value);
//    }
//
//    /**
//     * Get boolean property with default value.
//     */
//    public static boolean getBoolean(Environment env, String key, boolean defaultValue) {
//        String value = env.getProperty(key);
//        if (value == null || value.trim().isEmpty()) {
//            return defaultValue;
//        }
//        if ("true".equalsIgnoreCase(value.trim()) || "false".equalsIgnoreCase(value.trim())) {
//            return Boolean.parseBoolean(value.trim());
//        }
//        throw new IllegalStateException("Configuration property '" + key + "' must be 'true' or 'false': " + value);
//    }
//}