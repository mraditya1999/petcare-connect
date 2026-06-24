package com.spring.petcareConnect.exceptions;

import org.springframework.http.HttpStatus;

public class OtpException extends RuntimeException {

    private final HttpStatus status;

    public OtpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public OtpException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public static OtpException rateLimited(String message) {
        return new OtpException(message, HttpStatus.TOO_MANY_REQUESTS);
    }

    public static OtpException deliveryFailed(String message, Throwable cause) {
        return new OtpException(message, HttpStatus.SERVICE_UNAVAILABLE, cause);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
