package com.petconnect.backend.dto;

public class UserRegistrationResponse {
    private String message;

    public UserRegistrationResponse() {
    }

    public UserRegistrationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
