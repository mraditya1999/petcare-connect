package com.spring.petcareConnect.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.spring.petcareConnect.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"petOwner", "pet", "specialist"})
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_pet_owner", columnList = "pet_owner"),
        @Index(name = "idx_appointment_specialist", columnList = "specialist_id"),
        @Index(name = "idx_appointment_date", columnList = "appointment_date")
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ToString.Exclude
    @NotNull(message = "Pet owner reference cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_owner", nullable = false)
    @JsonBackReference
    private User petOwner;

    @ToString.Exclude
    @NotNull(message = "Pet reference cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonBackReference
    private Pet pet;

    @ToString.Exclude
    @NotNull(message = "Specialist reference cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @NotNull(message = "Appointment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", nullable = false, length = 20)
    private AppointmentStatus appointmentStatus;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Column(length = 1000)
    private String notes;

    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration cannot exceed 480 minutes")
    @Column(nullable = false)
    private Integer duration; // Duration in minutes (15-480 mins)

    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
    @Column(length = 1000)
    private String feedback;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    @Column(nullable = true)
    private Integer rating; // 1-5 stars

    @Column(name = "appointment_notes", length = 500)
    private String appointmentNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}