package com.spring.petcareConnect.dtos.appointment.response;

import com.spring.petcareConnect.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    private Long appointmentId;
    private Long petId;
    private String petName;
    private Long specialistId;
    private String specialistFirstName;
    private String specialistLastName;
    private LocalDateTime appointmentDate;
    private AppointmentStatus appointmentStatus;
    private String notes;
    private Integer duration;
    private Long petOwnerId;
    private String petOwnerFirstName;
    private String petOwnerLastName;

}