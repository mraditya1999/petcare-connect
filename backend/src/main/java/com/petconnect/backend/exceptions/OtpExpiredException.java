package com.petconnect.backend.exceptions;

public class OtpExpiredException extends ApiException {
    public OtpExpiredException(String message) {
        super(message);
    }
}