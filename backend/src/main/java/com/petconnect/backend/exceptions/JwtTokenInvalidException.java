package com.petconnect.backend.exceptions;

/**
 * Exception thrown when a JWT token is invalid or malformed.
 */
public class JwtTokenInvalidException extends JwtTokenException {
    public JwtTokenInvalidException(String message) {
        super("Invalid JWT token: " + message);
    }
}

