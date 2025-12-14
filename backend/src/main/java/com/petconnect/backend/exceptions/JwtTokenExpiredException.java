package com.petconnect.backend.exceptions;

/**
 * Exception thrown when a JWT token has expired.
 */
public class JwtTokenExpiredException extends JwtTokenException {
    public JwtTokenExpiredException() {
        super("JWT token has expired");
    }
}

