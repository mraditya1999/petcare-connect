package com.petconnect.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a Pet.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer petId;

    @NotNull(message = "Pet name cannot be null")
    @Size(min = 2, max = 50, message = "Pet name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String petName;

    @Min(value = 0, message = "Age cannot be less than 0")
    @Max(value = 30, message = "Age cannot exceed 30 years")
    private Integer age;

    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "300", message = "Weight cannot exceed 300 kg")
    private Double weight;

    private String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "petOwnerId", nullable = false)
    private User petOwner;

    @ManyToOne
    @JoinColumn(name = "speciesId", nullable = false)
    private Species species;

    @ManyToOne
    @JoinColumn(name = "breedId")
    private Breed breed;
}