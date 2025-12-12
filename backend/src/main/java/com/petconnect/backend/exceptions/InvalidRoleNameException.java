package com.petconnect.backend.exceptions;

public class InvalidRoleNameException extends ApiException {
    public InvalidRoleNameException(String message) {
        super(message);
    }
}
