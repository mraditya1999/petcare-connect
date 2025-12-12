package com.petconnect.backend.exceptions;

public class InvalidAddressException extends ApiException {
    public InvalidAddressException(String message) {
        super(message);
    }
}