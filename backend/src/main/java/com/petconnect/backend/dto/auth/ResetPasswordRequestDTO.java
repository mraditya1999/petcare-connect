package com.petconnect.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Token is mandatory")
    private String token;

    @NotBlank(message = "New password is mandatory")
    private String newPassword;
}
