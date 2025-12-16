package com.petconnect.backend.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Common utility methods for the application.
 * All methods are static for better reusability.
 */
public final class CommonUtils {

    private static final int DEFAULT_TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CommonUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Generate a URL-safe base64 token using secure random bytes.
     * @param numBytes number of random bytes to generate (must be > 0)
     * @return base64-url string without padding
     * @throws IllegalArgumentException if numBytes <= 0
     */
    public static String generateSecureToken(int numBytes) {
        if (numBytes <= 0) {
            throw new IllegalArgumentException("Number of bytes must be greater than 0");
        }
        byte[] randomBytes = new byte[numBytes];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Generate a URL-safe base64 token with default size (32 bytes).
     * @return base64-url string without padding
     */
    public static String generateSecureToken() {
        return generateSecureToken(DEFAULT_TOKEN_BYTES);
    }

    /**
     * Generate a secure random password token (32 bytes).
     * @return base64-url string without padding
     */
    public static String generateSecureRandomPassword() {
        return generateSecureToken(DEFAULT_TOKEN_BYTES);
    }
}
