package com.petconnect.backend.exceptions;

public class SpecialistNotFoundException extends ApiException {
    public SpecialistNotFoundException(String message) {
        super(message);
    }
}
