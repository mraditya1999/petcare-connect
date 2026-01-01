package com.petconnect.backend.validators;

import com.petconnect.backend.exceptions.ValidationException;
import com.petconnect.backend.utils.ValidationUtils;

/**
 * Base validator class providing common validation methods.
 * Validators can extend this class to inherit standard validation functionality.
 */
public abstract class BaseValidator {

    /**
     * Validate that an object is not null.
     */
    protected <T> T requireNotNull(T value, String fieldName) {
        return ValidationUtils.requireNotNull(value, fieldName);
    }

    /**
     * Validate that a string is not null or blank.
     */
    protected String requireNotBlank(String value, String fieldName) {
        return ValidationUtils.requireNotBlank(value, fieldName);
    }

    /**
     * Validate that a condition is true.
     */
    protected void requireTrue(boolean condition, String message) {
        ValidationUtils.requireTrue(condition, message);
    }

    /**
     * Validate using a custom predicate.
     */
    protected <T> T validate(T value, java.util.function.Predicate<T> predicate, String message) {
        return ValidationUtils.validate(value, predicate, message);
    }

    /**
     * Validate string length.
     */
    protected String validateLength(String value, int minLength, int maxLength, String fieldName) {
        return ValidationUtils.validateLength(value, minLength, maxLength, fieldName);
    }

    /**
     * Validate that a number is positive.
     */
    protected <T extends Number> T requirePositive(T value, String fieldName) {
        return ValidationUtils.requirePositive(value, fieldName);
    }

    /**
     * Throw validation exception with message.
     */
    protected void throwValidationException(String message) {
        throw new ValidationException(message);
    }
}