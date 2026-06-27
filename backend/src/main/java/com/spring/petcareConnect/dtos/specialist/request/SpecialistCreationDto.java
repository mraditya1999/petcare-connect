package com.spring.petcareConnect.dtos.specialist.request;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.enums.AvailableDay;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

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

    @NotNull(message = "Slot duration is required")
    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    @Max(value = 120, message = "Slot duration cannot exceed 120 minutes")
    private Integer slotDuration;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer experienceYears;

    @DecimalMin(value = "0.0", inclusive = false, message = "Consultation fee must be positive")
    private BigDecimal consultationFee;

    @NotNull(message = "Working hours start is required")
    private LocalTime workingHoursStart;

    @NotNull(message = "Working hours end is required")
    private LocalTime workingHoursEnd;

    @NotEmpty(message = "Days available must be provided")
    private Set<AvailableDay> daysAvailable;

    @NotBlank(message = "Location is required")
    private String location;
}
