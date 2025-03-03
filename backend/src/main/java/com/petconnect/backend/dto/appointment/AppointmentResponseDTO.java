package com.petconnect.backend.dto.appointment;

import com.petconnect.backend.entity.Appointment;
import lombok.Data;

import java.util.Date;

@Data
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

    public AppointmentResponseDTO() {
    }

    public AppointmentResponseDTO(Long appointmentId, Long petOwnerId, Long petId, Long specialistId, Date date, String notes, int duration, Appointment.AppointmentStatus status, String petOwnerName, String specialistName, String petName, String feedback, int rating) {
        this.appointmentId = appointmentId;
        this.petOwnerId = petOwnerId;
        this.petId = petId;
        this.specialistId = specialistId;
        this.date = date;
        this.notes = notes;
        this.duration = duration;
        this.status = status;
        this.petOwnerName = petOwnerName;
        this.specialistName = specialistName;
        this.petName = petName;
        this.feedback = feedback;
        this.rating = rating;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
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

    public Appointment.AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(Appointment.AppointmentStatus status) {
        this.status = status;
    }

    public String getPetOwnerName() {
        return petOwnerName;
    }

    public void setPetOwnerName(String petOwnerName) {
        this.petOwnerName = petOwnerName;
    }

    public String getSpecialistName() {
        return specialistName;
    }

    public void setSpecialistName(String specialistName) {
        this.specialistName = specialistName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}

