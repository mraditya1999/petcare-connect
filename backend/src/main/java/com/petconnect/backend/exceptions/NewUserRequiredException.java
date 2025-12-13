package com.petconnect.backend.exceptions;

public class NewUserRequiredException extends ApiException {
    public NewUserRequiredException(String message) {
        super(message);
    }
}
