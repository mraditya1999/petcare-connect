package com.spring.petcareConnect.dtos.auth.request;

import com.spring.petcareConnect.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX, message = AppConstants.PASSWORD_VALIDATION_MESSAGE)
    private String newPassword;
}
