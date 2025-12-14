package com.petconnect.backend.utils;

/**
 * Utility class for phone number operations.
 * Supports configurable country code for normalization via system property or default.
 */
public final class PhoneUtils {
    
    private static final String DEFAULT_COUNTRY_CODE = "+91"; // Default to India
    private static String configuredCountryCode = null;
    
    private PhoneUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Set the default country code for normalization.
     * This can be called during application initialization.
     * 
     * @param countryCode the country code in format like "+91"
     */
    public static void setDefaultCountryCode(String countryCode) {
        configuredCountryCode = countryCode;
    }
    
    private static String getDefaultCountryCode() {
        if (configuredCountryCode != null) {
            return configuredCountryCode;
        }
        // Try system property as fallback
        String systemProperty = System.getProperty("phone.default.country.code");
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }
        return DEFAULT_COUNTRY_CODE;
    }

    /**
     * Normalize phone number to E.164 format.
     * Uses configured default country code if 10-digit number is provided.
     * 
     * @param raw the raw phone number string
     * @return normalized E.164 format phone number, or null if invalid
     */
    public static String normalizeToE164(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D", "");
        String defaultCode = getDefaultCountryCode().replaceAll("\\D", "");
        
        // If 10-digit number, prepend default country code
        if (digits.length() == 10) {
            return "+" + defaultCode + digits;
        }
        // If starts with country code (without +) and length matches
        if (digits.length() == 12 && digits.startsWith(defaultCode)) {
            return "+" + digits;
        }
        // If already has leading + and looks like valid E.164
        if (raw.startsWith("+") && digits.length() >= 8) {
            return raw;
        }
        return null; // invalid
    }

    /**
     * Validate if phone number is in E.164 format.
     * 
     * @param phone the phone number to validate
     * @return true if valid E.164 format, false otherwise
     */
    public static boolean isValidE164(String phone) {
        if (phone == null) return false;
        return phone.matches("^\\+[1-9][0-9]{7,14}$");
    }
}
