package com.petconnect.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Breed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer breedId;

    @NotNull
    @Column(nullable = false)
    private String breedName;

    private Integer lifeSpan;

    @ManyToOne
    @JoinColumn(name = "speciesId", nullable = false)
    private Species species;
}