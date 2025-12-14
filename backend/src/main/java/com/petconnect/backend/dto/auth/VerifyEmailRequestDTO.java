package com.petconnect.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequestDTO {

    @NotBlank(message = "Verification token is mandatory")
    private String verificationToken;
}
