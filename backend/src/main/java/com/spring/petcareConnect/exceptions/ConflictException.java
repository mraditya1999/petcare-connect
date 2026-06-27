package com.spring.petcareConnect.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final HttpStatus status;

    public ConflictException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.CONFLICT;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
