package com.petconnect.backend.exceptions;

public class RoleNotFoundException extends ApiException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
