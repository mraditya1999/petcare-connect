package com.spring.petcareConnect.dtos.specialist.request;

import com.spring.petcareConnect.config.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistCreationDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX, message = AppConstants.PASSWORD_VALIDATION_MESSAGE)
    private String password;

    @NotBlank(message = "About section is required")
    @Size(min = 10, max = 500, message = "About section must be between 10 and 500 characters")
    private String about;
}