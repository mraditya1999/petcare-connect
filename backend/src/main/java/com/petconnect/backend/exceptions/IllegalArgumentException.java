package com.petconnect.backend.exceptions;

import org.springframework.http.HttpStatus;

public class IllegalArgumentException extends ApiException {
    public IllegalArgumentException(String message) {
        super(message);
    }

    public IllegalArgumentException(String message, HttpStatus status) {
        super(message, status);
    }
}
