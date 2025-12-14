package com.petconnect.backend.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {

    private Long petOwnerId;  

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Specialist ID is required")
    private Long specialistId;

    @NotNull(message = "Appointment date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    private String notes;

    @NotNull(message = "Duration is required")
    private int duration;
}
