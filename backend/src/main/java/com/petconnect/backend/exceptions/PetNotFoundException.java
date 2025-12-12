package com.petconnect.backend.exceptions;

public class PetNotFoundException extends ApiException {
    public PetNotFoundException(String message) {
        super(message);
    }
}