package com.petconnect.backend.exceptions;

public class InvalidRoleNameException extends RuntimeException {
    public InvalidRoleNameException(String message) {
        super(message);
    }
}
