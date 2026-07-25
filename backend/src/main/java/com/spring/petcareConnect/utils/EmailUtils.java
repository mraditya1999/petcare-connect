package com.spring.petcareConnect.utils;

import java.util.Locale;

public final class EmailUtils {
    private EmailUtils() {
    }

    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

