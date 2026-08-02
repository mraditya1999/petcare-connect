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
    public static final String OTP_SENT_SUCCESS = "OTP sent successfully";
    public static final String OTP_NEW_USER = "New user. Complete profile.";
    public static final String PROFILE_COMPLETED = "Profile completed successfully.";
    public static final String GOOGLE_OAUTH_URL = "Google OAuth URL generated.";
    public static final String GITHUB_OAUTH_URL = "Google OAuth URL generated.";

    // Pet
    public static final String PET_CREATED = "Pet has been created successfully.";
    public static final String PET_UPDATED = "Pet details updated successfully.";
    public static final String ALL_PETS_FETCHED = "All pets retrieved successfully.";
    public static final String PET_FETCHED = "Pet retrieved successfully.";
    public static final String PET_DELETED = "Pet deleted successfully.";

    // Appointment
    public static final String APPOINTMENT_CREATED = "Appointment has been created successfully.";
    public static final String APPOINTMENT_UPDATED = "Appointment updated successfully.";
    public static final String APPOINTMENT_CANCELLED = "Appointment cancelled successfully.";
    public static final String APPOINTMENTS_FETCHED = "All appointments retrieved successfully.";
    public static final String APPOINTMENT_FETCHED = "Appointment retrieved successfully.";
    public static final String APPOINTMENT_RESCHEDULED = "Appointment rescheduled successfully.";

    // Forum
    public static final String FORUMS_FETCHED = "All forums retrieved successfully.";
    public static final String FORUM_FETCHED = "Forum retrieved successfully.";
    public static final String FORUM_CREATED = "Forum has been created successfully.";
    public static final String FORUM_UPDATED = "Forum updated successfully.";
    public static final String FORUM_DELETED = "Forum deleted successfully.";
    public static final String FEATURED_FORUMS_FETCHED = "Top featured forums fetched successfully";

    // Comment
    public static final String COMMENT_CREATED = "Comment has been added successfully.";
    public static final String COMMENTS_FETCHED = "Comments retrieved successfully.";
    public static final String COMMENT_UPDATED = "Comment updated successfully.";
    public static final String COMMENT_DELETED = "Comment deleted successfully.";

    // Like
    public static final String FORUM_LIKED = "You have liked the forum successfully.";
    public static final String FORUM_UNLIKED = "You have removed your like from the forum.";
    public static final String COMMENT_LIKED = "You have liked the comment successfully.";
    public static final String COMMENT_UNLIKED = "You have removed your like from the comment.";


    // Specialist
    public static final String ALL_SPECIALISTS_FETCHED = "All Specialists retrieved successfully.";
    public static final String SPECIALIST_FETCHED = "Specialist retrieved successfully.";
    public static final String SPECIALIST_UPDATED = "Specialist updated successfully.";
    public static final String SPECIALIST_DASHBOARD_FETCHED = "Specialist dashboard retrieved successfully.";
}

