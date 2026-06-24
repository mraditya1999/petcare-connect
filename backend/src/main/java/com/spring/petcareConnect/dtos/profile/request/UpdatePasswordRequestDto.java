package com.spring.petcareConnect.dtos.profile.request;

import com.spring.petcareConnect.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX, message = AppConstants.PASSWORD_VALIDATION_MESSAGE)
    private String newPassword;
}
