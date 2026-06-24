package com.spring.petcareConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"breeds"})
@Entity
@Table(name = "species", indexes = {
        @Index(name = "idx_species_name", columnList = "species_name", unique = true)
})
public class Species {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "species_id")
    private Long speciesId;

    @NotBlank(message = "Species name is required")
    @Size(min = 1, max = 100, message = "Species name must be between 1 and 100 characters")
    @Column(name = "species_name", nullable = false, unique = true, length = 100)
    private String speciesName;

    @ToString.Exclude
    @OneToMany(mappedBy = "species", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Breed> breeds = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}