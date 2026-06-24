package com.spring.petcareConnect.dtos.oauth.request;

import com.spring.petcareConnect.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpLoginRequestDto {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = AppConstants.PHONE_REGEX, message = AppConstants.PHONE_VALIDATION_MESSAGE)
    private String phone;
}
