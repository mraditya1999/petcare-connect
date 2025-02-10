package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

public class Pet extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    @JsonIgnoreProperties("pets") // <-- Use the same reference name as in User.java
    private User petOwner;


//    @ManyToOne
//    @JoinColumn(name = "speciesId", nullable = false)
//    private Species species;
//
//    @ManyToOne
//    @JoinColumn(name = "breedId")
//    private Breed breed;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

//    public Species getSpecies() {
//        return species;
//    }
//
//    public void setSpecies(Species species) {
//        this.species = species;
//    }
//
//    public Breed getBreed() {
//        return breed;
//    }
//
//    public void setBreed(Breed breed) {
//        this.breed = breed;
//    }


    public User getPetOwner() {
        return petOwner;
    }

    public void setPetOwner(User petOwner) {
        this.petOwner = petOwner;
    }

    public Pet() {
    }

    public Pet(Long petId, String petName, Integer age, Double weight, User petOwner) {
        this.petId = petId;
        this.petName = petName;
        this.age = age;
        this.weight =
                weight;
        this.petOwner = petOwner;
    }
}