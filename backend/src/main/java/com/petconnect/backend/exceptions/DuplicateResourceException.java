package com.petconnect.backend.exceptions;

public class DuplicateResourceException extends ApiException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
