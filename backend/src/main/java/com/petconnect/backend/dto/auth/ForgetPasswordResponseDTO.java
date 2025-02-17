package com.petconnect.backend.dto.auth;

public class ForgetPasswordResponseDTO {

    private String message;

    public ForgetPasswordResponseDTO() {
    }

    public ForgetPasswordResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
