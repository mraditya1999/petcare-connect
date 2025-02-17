package com.petconnect.backend.dto.auth;

public class UserRegistrationResponseDTO {

    private String message;

    public UserRegistrationResponseDTO() {
    }

    public UserRegistrationResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}