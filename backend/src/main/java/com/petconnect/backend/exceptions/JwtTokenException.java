package com.petconnect.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Base exception for JWT token-related errors.
 */
public class JwtTokenException extends AuthenticationException {
    public JwtTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

