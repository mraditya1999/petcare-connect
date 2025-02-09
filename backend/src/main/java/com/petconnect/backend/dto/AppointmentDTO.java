package com.petconnect.backend.dto;

import com.petconnect.backend.entity.Appointment;
import java.util.Date;

public class AppointmentDTO {
    private Long appointmentId;
    private Date date;
    private Long petId;
    private Long specialistId;
    private Long userId;

    public AppointmentDTO() {}

    public AppointmentDTO(Long appointmentId, Date date, Long petId, Long specialistId, Long userId) {
        this.appointmentId = appointmentId;
        this.date = date;
        this.petId = petId;
        this.specialistId = specialistId;
        this.userId = userId;
    }

    // ✅ FIXED: Properly maps Appointment entity to DTO
    public AppointmentDTO(Appointment appointment) {
        this.appointmentId = appointment.getAppointmentId();
        this.date = appointment.getDate();
        this.petId = (appointment.getPet() != null) ? appointment.getPet().getPetId() : null;
        this.specialistId = (appointment.getSpecialist() != null) ? appointment.getSpecialist().getSpecialistId() : null;
        this.userId = (appointment.getPetOwner() != null) ? appointment.getPetOwner().getUserId() : null;
    }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getSpecialistId() { return specialistId; }
    public void setSpecialistId(Long specialistId) { this.specialistId = specialistId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
