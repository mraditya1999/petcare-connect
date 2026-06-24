package com.spring.petcareConnect.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PhoneUtils {

    private static final Logger logger = LoggerFactory.getLogger(PhoneUtils.class);

    private PhoneUtils() {
        // Utility class - prevent instantiation
    }

    public static String normalizeToIndianFormat(String raw) {
        if (raw == null) {
            logger.error("Phone normalization failed: input is null");
            return null;
        }

        // Remove all non-digit characters
        String digits = raw.replaceAll("\\D", "");
        logger.debug("Normalized input={} to digits={}", raw, digits);

        // Case 1: 10-digit mobile number (India local format)
        if (digits.length() == 10) {
            logger.info("Phone number recognized as local 10-digit format: {}", digits);
            return digits; // store only the 10 digits
        }

        // Case 2: Already has +91 prefix (e.g., +916306374456)
        if (raw.startsWith("+91") && digits.length() == 12) {
            logger.info("Phone number recognized with +91 prefix: {}", raw);
            return digits.substring(2); // strip +91, keep last 10 digits
        }

        // Case 3: If someone passes 91XXXXXXXXXX without +
        if (digits.length() == 12 && digits.startsWith("91")) {
            logger.info("Phone number recognized with 91 prefix (no +): {}", raw);
            return digits.substring(2); // strip 91, keep last 10 digits
        }

        logger.error("Phone normalization failed: invalid format for input={}", raw);
        return null;
    }

    public static boolean isValidIndianMobile(String phone) {
        if (phone == null) {
            logger.error("Phone validation failed: input is null");
            return false;
        }
        boolean valid = phone.matches("^\\d{10}$");
        if (valid) {
            logger.info("Phone number is valid Indian mobile: {}", phone);
        } else {
            logger.warn("Phone number is invalid Indian mobile: {}", phone);
        }
        return valid;
    }

    public static String toIndianE164(String normalizedPhone) {
        if (!isValidIndianMobile(normalizedPhone)) {
            logger.error("E.164 conversion failed: invalid Indian mobile number");
            return null;
        }
        return "+91" + normalizedPhone;
    }
}
