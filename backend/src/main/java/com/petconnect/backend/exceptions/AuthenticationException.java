package com.petconnect.backend.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApiException {
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, HttpStatus status) {
        super(message, status);
    }
}
