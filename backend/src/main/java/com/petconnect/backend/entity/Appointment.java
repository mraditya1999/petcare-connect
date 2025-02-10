package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name= "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "petId", nullable = false)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "specialistId", nullable = false)
    private Specialist specialist;

//    @ManyToOne
//    @JoinColumn(name = "petOwnerId", nullable = false)
//    private User petOwner;
@ManyToOne
@JoinColumn(name = "petOwnerId", referencedColumnName = "userId", nullable = false)
@JsonBackReference
private User petOwner;


    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public Appointment() {
    }

    public Appointment(Long appointmentId, Date date, Date createdAt, Date updatedAt, Pet pet, Specialist specialist, User petOwner) {
        this.appointmentId = appointmentId;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pet = pet;
        this.specialist = specialist;
        this.petOwner = petOwner;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public User getPetOwner() {
        return petOwner;
    }

    public void setPetOwner(User petOwner) {
        this.petOwner = petOwner;
    }
}
