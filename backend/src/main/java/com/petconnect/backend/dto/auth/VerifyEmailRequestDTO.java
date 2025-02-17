package com.petconnect.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class VerifyEmailRequestDTO {

    @NotBlank(message = "Verification token is mandatory")
    private String verificationToken;

    public VerifyEmailRequestDTO() {}

    public VerifyEmailRequestDTO(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}
