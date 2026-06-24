package com.spring.petcareConnect.utils;

import java.util.Locale;

public final class EmailUtils {
    private EmailUtils() {
    }

    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}

