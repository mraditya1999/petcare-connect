package com.petconnect.backend.exceptions;

public class UnauthorizedAccessException extends ApiException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
