package com.spring.petcareConnect.security.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Security-related utility methods for secure token, password and OTP generation.
 */
public final class SecurityUtils {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*-_";

    private SecurityUtils() {}

    /**
     * Generate a URL-safe secure token encoded in Base64 without padding.
     * @param numBytes number of random bytes
     */
    public static String generateSecureToken(int numBytes) {
        if (numBytes <= 0) throw new IllegalArgumentException("numBytes must be > 0");
        byte[] b = new byte[numBytes];
        secureRandom.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    /**
     * Generate a random password of given length using a safe charset.
     */
    public static String generateRandomPassword(int length) {
        if (length <= 0) throw new IllegalArgumentException("length must be > 0");
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(PASSWORD_CHARS.length());
            sb.append(PASSWORD_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    /**
     * Generate a numeric OTP of specified length using SecureRandom.
     */
    public static String generateNumericOtp(int length) {
        if (length <= 0) throw new IllegalArgumentException("length must be > 0");
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int val = secureRandom.nextInt((max - min) + 1) + min;
        return String.format("%0" + length + "d", val);
    }

}

