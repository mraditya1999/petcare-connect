package com.petconnect.backend.dto.appointment;

import com.petconnect.backend.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Long appointmentId;
    private Long petOwnerId;
    private Long petId;
    private Long specialistId;
    private Date date;
    private String notes;
    private int duration;
    private Appointment.AppointmentStatus status;

    private String petOwnerName;
    private String specialistName;
    private String petName;

    // New fields for feedback and rating
    private String feedback;
    private int rating;
}

