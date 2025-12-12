package com.petconnect.backend.utils;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CommonUtils {
    /**
     * Generate a URL-safe base64 token using secure random bytes.
     * @param numBytes number of random bytes to generate
     * @return base64-url string without padding
     */
    public String generateSecureToken(int numBytes) {
        byte[] randomBytes = new byte[numBytes];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /** Convenience: 32-byte token (default) */
    public String generateSecureToken() {
        return generateSecureToken(32);
    }

    /** Convenience: password-length token generator */
    public String generateSecureRandomPassword() {
        return generateSecureToken(32);
    }
}
