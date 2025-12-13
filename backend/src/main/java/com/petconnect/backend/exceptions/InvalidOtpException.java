package com.petconnect.backend.exceptions;

public class InvalidOtpException extends ApiException {
    public InvalidOtpException(String message) {
        super(message);
    }
}