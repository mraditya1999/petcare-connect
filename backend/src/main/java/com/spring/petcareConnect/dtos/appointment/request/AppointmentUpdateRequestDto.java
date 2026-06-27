package com.spring.petcareConnect.dtos.appointment.request;

import com.spring.petcareConnect.enums.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUpdateRequestDto {
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDateTime appointmentDate;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    private AppointmentStatus appointmentStatus;
}
