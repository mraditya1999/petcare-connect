package com.spring.petcareConnect.dtos.appointment.request;

import com.spring.petcareConnect.enums.AppointmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDto {

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Specialist ID is required")
    private Long specialistId;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDateTime appointmentDate;

    @NotNull(message = "Appointment status is required")
    private AppointmentStatus appointmentStatus;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}

