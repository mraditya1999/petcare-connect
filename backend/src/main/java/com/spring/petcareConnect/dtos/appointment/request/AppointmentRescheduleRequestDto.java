package com.spring.petcareConnect.dtos.appointment.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRescheduleRequestDto {
    @NotNull(message = "New appointment date is required")
    private LocalDateTime newAppointmentDate;
}

