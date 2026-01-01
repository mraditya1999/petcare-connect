package com.petconnect.backend.utils;

/**
 * Utility class for Redis key generation and common operations.
 * Provides consistent key patterns and TTL management across Redis services.
 */
public final class RedisUtils {

    private RedisUtils() {
        // Utility class
    }

    // Common key prefixes
    public static final String OTP_PREFIX = "otp:";
    public static final String TEMP_USER_PREFIX = "tempUser:";
    public static final String SESSION_PREFIX = "session:";
    public static final String CACHE_PREFIX = "cache:";
    public static final String VERIFY_TOKEN_PREFIX = "token:verify:";
    public static final String RESET_TOKEN_PREFIX = "token:reset:";
    public static final String OAUTH_STATE_PREFIX = "oauth:state:";

    /**
     * Generate OTP key for phone number.
     */
    public static String otpKey(String phone) {
        validateNotBlank(phone, "Phone cannot be null or blank");
        return OTP_PREFIX + phone;
    }

    /**
     * Generate temporary user key for token.
     */
    public static String tempUserKey(String token) {
        validateNotBlank(token, "Token cannot be null or blank");
        return TEMP_USER_PREFIX + token;
    }

    /**
     * Generate session key for user ID.
     */
    public static String sessionKey(String userId) {
        validateNotBlank(userId, "User ID cannot be null or blank");
        return SESSION_PREFIX + userId;
    }

    /**
     * Generate cache key with prefix.
     */
    public static String cacheKey(String key) {
        validateNotBlank(key, "Cache key cannot be null or blank");
        return CACHE_PREFIX + key;
    }

    /**
     * Generate verification token key.
     */
    public static String verifyTokenKey(String token) {
        validateNotBlank(token, "Token cannot be null or blank");
        return VERIFY_TOKEN_PREFIX + token;
    }

    /**
     * Generate password reset token key.
     */
    public static String resetTokenKey(String token) {
        validateNotBlank(token, "Token cannot be null or blank");
        return RESET_TOKEN_PREFIX + token;
    }

    /**
     * Generate OAuth state key.
     */
    public static String oauthStateKey(String state) {
        validateNotBlank(state, "State cannot be null or blank");
        return OAUTH_STATE_PREFIX + state;
    }

    /**
     * Generate rate limiting key.
     */
    public static String rateLimitKey(String identifier, String action) {
        validateNotBlank(identifier, "Identifier cannot be null or blank");
        validateNotBlank(action, "Action cannot be null or blank");
        return "ratelimit:" + action + ":" + identifier;
    }

    private static void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}