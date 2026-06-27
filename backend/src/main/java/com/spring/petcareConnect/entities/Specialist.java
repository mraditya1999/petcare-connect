package com.spring.petcareConnect.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.spring.petcareConnect.enums.AvailableDay;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"appointments"})
@Entity
@Table(name = "specialists", indexes = {
        @Index(name = "idx_specialist_user", columnList = "user_id", unique = true)
})
public class Specialist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialist_id")
    private Long specialistId;

    @NotNull(message = "User reference cannot be null")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "About section is required")
    @Size(min = 10, max = 500, message = "About section must be between 10 and 500 characters")
    @Column(nullable = false, length = 500)
    private String about;

    @Column(nullable = false)
    private boolean available = true; // specialist availability toggle

    // NEW FIELDS
    @Column(name = "slot_duration", nullable = false)
    private Integer slotDuration = 30; // default 30 minutes

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "consultation_fee")
    private BigDecimal consultationFee;

    @Column(name = "working_hours_start")
    private LocalTime workingHoursStart;

    @Column(name = "working_hours_end")
    private LocalTime workingHoursEnd;

    @ElementCollection(targetClass = AvailableDay.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "specialist_days_available", joinColumns = @JoinColumn(name = "specialist_id"))
    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<AvailableDay> daysAvailable;

    @Column(name = "location", length = 200)
    private String location;

    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}