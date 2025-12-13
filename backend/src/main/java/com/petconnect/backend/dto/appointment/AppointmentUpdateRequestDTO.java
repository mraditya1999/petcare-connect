package com.petconnect.backend.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.petconnect.backend.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Data
public class AppointmentUpdateRequestDTO {

    @NotNull(message = "Appointment date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    private String notes;

    @NotNull(message = "Duration is required")
    private Integer duration;

    @NotNull(message = "Status is required")
    private Appointment.AppointmentStatus status;

    public AppointmentUpdateRequestDTO() {
    }

    public AppointmentUpdateRequestDTO(Date date, String notes, Integer duration, Appointment.AppointmentStatus status) {
        this.date = date;
        this.notes = notes;
        this.duration = duration;
        this.status = status;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Appointment.AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(Appointment.AppointmentStatus status) {
        this.status = status;
    }
}
