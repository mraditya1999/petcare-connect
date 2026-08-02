package com.spring.petcareConnect.dtos.specialist.request;

import com.spring.petcareConnect.enums.AvailableDay;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSpecialistUpdateRequest {

    @NotBlank(message = "About section is required")
    private String about;

    @NotNull(message = "Availability flag is required")
    private Boolean available;

    @NotNull(message = "Slot duration is required")
    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
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

    @NotNull(message = "Days available must be provided")
    private Set<AvailableDay> daysAvailable;

    @NotBlank(message = "Location is required")
    private String location;
}
