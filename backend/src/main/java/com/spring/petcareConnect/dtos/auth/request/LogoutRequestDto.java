package com.spring.petcareConnect.dtos.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDto {
    @NotBlank(message = "Refresh token must not be blank")
    @Size(max = 500, message = "Refresh token length cannot exceed 500 characters")
    private String refreshToken;
}
