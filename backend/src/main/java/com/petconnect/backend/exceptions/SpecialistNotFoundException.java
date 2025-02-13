package com.petconnect.backend.exceptions;

public class SpecialistNotFoundException extends RuntimeException {
    public SpecialistNotFoundException(String message) {
        super(message);
    }
}
