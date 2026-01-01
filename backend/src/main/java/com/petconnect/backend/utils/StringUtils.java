package com.petconnect.backend.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for string operations.
 * Provides common string manipulation and validation methods.
 */
public final class StringUtils {

    private StringUtils() {
        // Utility class
    }

    /**
     * Common regex patterns.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9][0-9]{7,14}$"
    );

    /**
     * Check if string is null or empty.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Check if string is null, empty, or contains only whitespace.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if string is not null and not empty.
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Check if string is not null, not empty, and not blank.
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Capitalize first letter of string.
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Convert to title case.
     */
    public static String toTitleCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return Arrays.stream(str.split("\\s+"))
                .map(StringUtils::capitalize)
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }

    /**
     * Truncate string to max length with ellipsis.
     */
    public static String truncate(String str, int maxLength) {
        if (isEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Validate email format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format (E.164).
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Remove all non-numeric characters.
     */
    public static String extractDigits(String str) {
        return str != null ? str.replaceAll("\\D", "") : null;
    }

    /**
     * Split string by comma and trim whitespace.
     */
    public static List<String> splitByComma(String str) {
        if (isEmpty(str)) {
            return List.of();
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .toList();
    }

    /**
     * Join strings with comma separator.
     */
    public static String joinWithComma(List<String> strings) {
        return strings != null ? String.join(", ", strings) : null;
    }

    /**
     * Generate random alphanumeric string.
     */
    public static String generateRandomString(int length) {
        ValidationUtils.requireTrue(length > 0, "Length must be positive");
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    /**
     * Mask sensitive information (e.g., email, phone).
     */
    public static String maskSensitive(String str) {
        if (isEmpty(str) || str.length() < 4) {
            return str;
        }

        int visibleChars = Math.min(2, str.length() / 4);
        int maskLength = str.length() - (visibleChars * 2);

        return str.substring(0, visibleChars) +
               "*".repeat(maskLength) +
               str.substring(str.length() - visibleChars);
    }
}