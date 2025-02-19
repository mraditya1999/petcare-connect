package com.petconnect.backend.exceptions;

public class DuplicatePetNameException extends RuntimeException {
    public DuplicatePetNameException(String message) {
        super(message);
    }
}
