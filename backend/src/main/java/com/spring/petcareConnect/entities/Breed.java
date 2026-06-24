package com.spring.petcareConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"species"})
@Entity
@Table(name = "breeds", indexes = {
        @Index(name = "idx_breed_species", columnList = "species_id")
},
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_breed_species", columnNames = {"breed_name", "species_id"})
        })
public class Breed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breed_id")
    private Long breedId;

    @NotBlank(message = "Breed name is required")
    @Size(min = 1, max = 50, message = "Breed name must be between 1 and 50 characters")
    @Column(name = "breed_name", nullable = false, length = 50)
    private String breedName;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}