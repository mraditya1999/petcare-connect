package com.petconnect.backend.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.petconnect.backend.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUpdateRequestDTO {

    @NotNull(message = "Appointment date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    private String notes;

    @NotNull(message = "Duration is required")
    private Integer duration;

    @NotNull(message = "Status is required")
    private Appointment.AppointmentStatus status;
}
