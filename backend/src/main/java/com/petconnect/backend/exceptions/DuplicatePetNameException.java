package com.petconnect.backend.exceptions;

public class DuplicatePetNameException extends ApiException {
    public DuplicatePetNameException(String message) {
        super(message);
    }
}
