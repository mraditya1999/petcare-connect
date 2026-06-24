package com.spring.petcareConnect.config;

/**
 * Centralized response message constants to avoid magic strings across controllers and handlers.
 */
public final class ResponseMessages {

    private ResponseMessages() { }

    // Auth-related
    public static final String REGISTRATION_SENT_EMAIL = "Please check your email for the verification link.";
    public static final String EMAIL_VERIFIED_ADMIN = "Email verified. You have been designated as Administrator.";
    public static final String EMAIL_VERIFIED_SUCCESS = "Email verified successfully.";
    public static final String LOGIN_SUCCESS = "User logged in successfully.";
    public static final String REFRESH_SUCCESS = "Access token refreshed successfully.";
    public static final String LOGOUT_SUCCESS = "User logged out successfully.";
    public static final String PASSWORD_RESET_SENT = "Password reset email sent successfully.";
    public static final String PASSWORD_RESET_SUCCESS = "Password reset successfully.";

    // Profile-related
    public static final String PROFILE_FETCHED = "Profile fetched successfully";
    public static final String PROFILE_UPDATED = "Profile updated successfully";
    public static final String PROFILE_DELETED = "Profile deleted successfully";
    public static final String PASSWORD_UPDATED_PREFIX = "Password has been updated successfully for user";

    // OAuth / OTP
    public static final String OAUTH_LOGIN_PROCESSED = "OAuth login processed";
    public static final String OTP_SENT_SUCCESS = "OTP sent successfully";
    public static final String OTP_NEW_USER = "New user. Complete profile.";
    public static final String PROFILE_COMPLETED = "Profile completed successfully.";

    // Specialist


}

