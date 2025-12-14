package com.petconnect.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordRequestDTO {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
}
