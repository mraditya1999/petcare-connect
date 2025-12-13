package com.petconnect.backend.exceptions;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
