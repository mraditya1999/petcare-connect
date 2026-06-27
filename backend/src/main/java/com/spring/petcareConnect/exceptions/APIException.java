package com.spring.petcareConnect.exceptions;

public class APIException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public APIException() {
        super();
    }

    public APIException(String message) {
        super("API error: " + message);
    }

    public APIException(String message, Throwable cause) {
        super("API error: " + message, cause);
    }

}
