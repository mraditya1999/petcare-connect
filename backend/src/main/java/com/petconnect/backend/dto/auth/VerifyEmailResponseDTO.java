package com.petconnect.backend.dto.auth;

public class VerifyEmailResponseDTO {

    private String message;
    private boolean isVerified;

    public VerifyEmailResponseDTO() {
    }

    public VerifyEmailResponseDTO(String message, boolean isVerified) {
        this.message = message;
        this.isVerified = isVerified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}
