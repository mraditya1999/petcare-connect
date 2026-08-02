package com.spring.petcareConnect.dtos.specialist.request;

import com.spring.petcareConnect.enums.AvailableDay;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistProfileUpdateRequestDto {

    @Size(min = 10, max = 500, message = "About section must be between 10 and 500 characters")
    private String about;

    private Boolean available;

    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    private Integer slotDuration;

    private String specialization;

    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer experienceYears;

    @DecimalMin(value = "0.0", inclusive = false, message = "Consultation fee must be positive")
    private BigDecimal consultationFee;

    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    private Set<AvailableDay> daysAvailable;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;
}
