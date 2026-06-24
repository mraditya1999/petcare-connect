package com.spring.petcareConnect.dtos.oauth.request;

import com.spring.petcareConnect.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequestDto {
    @NotBlank(message = "phone is required")
    @Pattern(regexp = AppConstants.PHONE_REGEX, message = AppConstants.PHONE_VALIDATION_MESSAGE)
    private String phone;

    @NotBlank(message = "otp is required")
    @Size(min = 4, max = 6, message = "otp must be between 4 and 6 characters")
    @Pattern(regexp = "^[0-9]+$", message = "otp must contain only digits")
    private String otp;
}

