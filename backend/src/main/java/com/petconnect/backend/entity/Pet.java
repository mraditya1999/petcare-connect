package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"petOwner"})
@EqualsAndHashCode(exclude = {"petOwner"})
@Entity
@NamedEntityGraph(
    name = "Pet.withOwner",
    attributeNodes = {
        @NamedAttributeNode("petOwner")
    }
)
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull(message = "Breed cannot be null")
    @Size(min = 1, max = 50, message = "Breed must be between 1 and 50 characters")
    @Column(nullable = false)
    private String breed;

    @NotNull(message = "Species cannot be null")
    @Size(min = 1, max = 50, message = "Species must be between 1 and 50 characters")
    @Column(nullable = false)
    private String species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    @JsonBackReference
    private User petOwner;

    @Column
    private String avatarUrl;

    @Column
    private String avatarPublicId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

    public enum Gender {
        MALE, FEMALE
    }
}
