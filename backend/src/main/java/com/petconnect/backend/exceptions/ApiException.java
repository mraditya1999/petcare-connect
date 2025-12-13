package com.petconnect.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Base exception for API-level errors.
 * Subclass existing custom exceptions from this class to carry an HTTP status and
 * produce a consistent response shape via the global exception handler.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(String message) {
        this(message, HttpStatus.BAD_REQUEST, null, null);
    }

    public ApiException(String message, HttpStatus status) {
        this(message, status, null, null);
    }

    public ApiException(String message, Throwable cause) {
        this(message, HttpStatus.BAD_REQUEST, null, cause);
    }

    public ApiException(String message, HttpStatus status, String code) {
        this(message, status, code, null);
    }

    public ApiException(String message, HttpStatus status, String code, Throwable cause) {
        super(message, cause);
        this.status = status == null ? HttpStatus.BAD_REQUEST : status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
