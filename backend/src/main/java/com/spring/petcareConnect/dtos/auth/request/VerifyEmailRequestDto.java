package com.spring.petcareConnect.dtos.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailRequestDto {

    @NotBlank(message = "Verification token is required")
    private String verificationToken;
}