package com.petconnect.backend.utils;

public final class PhoneUtils {
    private PhoneUtils() {}

    /** Normalize to E.164 for India (if 10-digit provided) or pass-through if already starts with + */
    public static String normalizeToE164(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() == 10) {
            return "+91" + digits;
        }
        // if starts with 91 and length 12 -> +91...
        if (digits.length() == 12 && digits.startsWith("91")) {
            return "+" + digits;
        }
        // if already has leading + and looks like +<digits>
        if (raw.startsWith("+") && raw.replaceAll("\\D", "").length() >= 8) {
            return raw;
        }
        return null; // invalid
    }

    public static boolean isValidE164(String phone) {
        if (phone == null) return false;
        return phone.matches("^\\+[1-9][0-9]{7,14}$");
    }
}
