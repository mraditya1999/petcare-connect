package com.petconnect.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyOtpRequestDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$",message = "Invalid phone number format")
    private final String phone;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{4,6}$",message = "OTP must be 4 to 6 digits")
    private final String otp;
}
