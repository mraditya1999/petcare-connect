package com.spring.petcareConnect.config;

import java.util.List;

public class AppConstants {
    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "50";
    public static final String SORT_CATEGORIES_BY = "categoryId";
    public static final String SORT_PRODUCTS_BY = "productId";
    public static final String SORT_DIR = "asc";

    // File validation constants
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final List<String> ALLOWED_FILE_FORMATS = List.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/pdf"
    );

    // OTP / Redis key prefixes
    public static final String OTP_KEY_PREFIX = "otp:";
    public static final String OTP_ATTEMPTS_KEY_PREFIX = "otp:attempts:";
    public static final String OTP_BLOCKED_KEY_PREFIX = "otp:blocked:";
    public static final String OTP_LAST_SENT_PREFIX = "otp:lastsent:"; // value = epoch seconds
    public static final String OTP_HOURLY_COUNT_PREFIX = "otp:count:"; // otp:count:{phone}:{yyyyMMddHH}

    // JWT temp token purposes
    public static final String TOKEN_PURPOSE_LOGIN = "LOGIN";
    public static final String TOKEN_PURPOSE_EMAIL_VERIFICATION = "EMAIL_VERIFICATION";
    public static final String TOKEN_PURPOSE_RESET_PASSWORD = "RESET_PASSWORD";
    public static final String TEMP_TOKEN_PURPOSE_PROFILE_COMPLETION = "PROFILE_COMPLETION";


    // Security
    public static final int SECURE_BYTES = 32; // default length for secure token generation

    // Phone validation
    public static final String PHONE_REGEX = "^[0-9]{10}$"; // exactly 10 digits, no + prefix for consistency with DB
    public static final String PHONE_VALIDATION_MESSAGE = "phone must be exactly 10 digits";

    // Password validation (min 8 chars, at least one uppercase, one lowercase, one digit, one special char)
    public static final String PASSWORD_REGEX =
            "^(?=.{8,}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$";
    public static final String PASSWORD_VALIDATION_MESSAGE =
            "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character";


        // Providers
        public static final String GOOGLE = "Google";
        public static final String GITHUB = "GitHub";

        // Token URLs
        public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
        public static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";

        // Auth URLs
        public static final String GOOGLE_AUTH_URL_TEMPLATE =
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&access_type=offline&prompt=select_account";
        public static final String GITHUB_AUTH_URL_TEMPLATE =
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s&state=%s&allow_signup=true";

        // Scopes
        public static final String GOOGLE_SCOPE = "openid email profile";
        public static final String GITHUB_SCOPE = "user:email";

        // Expiry
        public static final long OAUTH_TOKEN_EXPIRY_SECONDS = 3600L;

        // Profile endpoints
        public static final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
        public static final String GITHUB_PROFILE_URL = "https://api.github.com/user";

}
