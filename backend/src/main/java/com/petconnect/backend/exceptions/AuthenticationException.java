package com.petconnect.backend.exceptions;

public class AuthenticationException extends ApiException {
    public AuthenticationException(String message) {
        super(message);
    }
}
