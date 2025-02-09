package com.petconnect.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {
    private Long petId;

    @NotBlank(message = "Pet name cannot be blank")
    private String petName;

    @Min(value = 0, message = "Age must be a positive number")
    private Integer age;

    @Min(value = 0, message = "Weight must be a positive number")
    private Double weight;

    private String avatarUrl;

    @NotNull(message = "Pet owner ID cannot be null")
    private Long petOwnerId; // Only store the owner's ID, not the full User entity

    @NotBlank(message = "Species cannot be blank")
    private String species;

    @NotBlank(message = "Breed cannot be blank")
    private String breed;

    @Override
    public String toString() {
        return "PetDTO{" +
                "petId=" + petId +
                ", petName='" + petName + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", petOwnerId=" + petOwnerId +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                '}';
    }

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getPetOwnerId() {
        return petOwnerId;
    }

    public void setPetOwnerId(Long petOwnerId) {
        this.petOwnerId = petOwnerId;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
