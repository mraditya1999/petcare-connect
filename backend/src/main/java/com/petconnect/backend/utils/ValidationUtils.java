package com.petconnect.backend.utils;

import com.petconnect.backend.exceptions.ValidationException;

import java.util.function.Predicate;

/**
 * Generic validation utility for common validation patterns.
 * Provides reusable validation methods that throw ValidationException.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class
    }

    /**
     * Validate that a value is not null.
     */
    public static <T> T requireNotNull(T value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
        return value;
    }

    /**
     * Validate that a string is not null or blank.
     */
    public static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " cannot be null or blank");
        }
        return value;
    }

    /**
     * Validate that a condition is true.
     */
    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validate using a custom predicate.
     */
    public static <T> T validate(T value, Predicate<T> predicate, String message) {
        if (!predicate.test(value)) {
            throw new ValidationException(message);
        }
        return value;
    }

    /**
     * Validate string length.
     */
    public static String validateLength(String value, int minLength, int maxLength, String fieldName) {
        requireNotNull(value, fieldName);
        if (value.length() < minLength || value.length() > maxLength) {
            throw new ValidationException(fieldName + " must be between " + minLength + " and " + maxLength + " characters");
        }
        return value;
    }

    /**
     * Validate that a number is positive.
     */
    public static <T extends Number> T requirePositive(T value, String fieldName) {
        requireNotNull(value, fieldName);
        if (value.doubleValue() <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
        return value;
    }
}