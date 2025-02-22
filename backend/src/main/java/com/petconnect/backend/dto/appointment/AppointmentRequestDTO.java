package com.petconnect.backend.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class AppointmentRequestDTO {

    private Long petOwnerId;  // Optional field for pet owner ID

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

    public AppointmentRequestDTO() {
    }

    public AppointmentRequestDTO(Long petOwnerId, Long petId, Long specialistId, Date date, String notes, int duration) {
        this.petOwnerId = petOwnerId;
        this.petId = petId;
        this.specialistId = specialistId;
        this.date = date;
        this.notes = notes;
        this.duration = duration;
    }

    public Long getPetOwnerId() {
        return petOwnerId;
    }

    public void setPetOwnerId(Long petOwnerId) {
        this.petOwnerId = petOwnerId;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(Long specialistId) {
        this.specialistId = specialistId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
