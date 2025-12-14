package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"appointments"})
@Entity
@Table(name = "specialists")
@PrimaryKeyJoinColumn(name = "specialist_id")
@SuperBuilder
public class Specialist extends User {

    @Column(nullable = false, length = 500)
    private String about;

    @Column(nullable = false, length = 100)
    private String speciality;

    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    @BatchSize(size = 20)
    private List<Appointment> appointments;

    // Convenience getter for specialistId (same as userId)
    public Long getSpecialistId() {
        return getUserId();
    }
}
