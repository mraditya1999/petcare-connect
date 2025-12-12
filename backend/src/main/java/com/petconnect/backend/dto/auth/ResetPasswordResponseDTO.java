package com.petconnect.backend.dto.auth;


public class ResetPasswordResponseDTO {

    private String message;

    public ResetPasswordResponseDTO() {
    }

    public ResetPasswordResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
