package com.spring.petcareConnect.utils;

import com.spring.petcareConnect.security.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class AuthUtils {
    private AuthUtils() {}

    public static Optional<UserDetailsImpl> loggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

    public static Optional<Long> loggedInUserId() {
        return loggedInUser().map(UserDetailsImpl::getId);
    }

    public static Optional<String> loggedInEmail() {
        return loggedInUser().map(UserDetailsImpl::getEmail);
    }
}
