package com.petconnect.backend.dto.pet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PetResponseDTO {

    private Long petId;

    @NotNull(message = "Pet name cannot be null")
    @Size(min = 1, max = 255, message = "Pet name must be between 1 and 255 characters")
    private String petName;

    @NotNull(message = "Age cannot be null")
    @Positive(message = "Age must be a positive number")
    private Integer age;

    @NotNull(message = "Weight cannot be null")
    @Positive(message = "Weight must be a positive number")
    private Double weight;

    private String avatarUrl;
    private String avatarPublicId;

    @NotNull(message = "Gender cannot be null")
    @Size(min = 1, max = 10, message = "Gender must be between 1 and 10 characters")
    private String gender;

    @NotNull(message = "Breed cannot be null")
    @Size(min = 1, max = 255, message = "Breed must be between 1 and 255 characters")
    private String breed;

    @NotNull(message = "Species cannot be null")
    @Size(min = 1, max = 255, message = "Species must be between 1 and 255 characters")
    private String species;

    public PetResponseDTO() {
    }

      public PetResponseDTO(Long petId, String petName, Integer age, Double weight, String avatarUrl, String avatarPublicId, String gender, String breed, String species) {
        this.petId = petId;
        this.petName = petName;
        this.age = age;
        this.weight = weight;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
        this.gender = gender;
        this.breed = breed;
        this.species = species;
    }

    // Getters and Setters
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

    public String getAvatarPublicId() {
        return avatarPublicId;
    }

    public void setAvatarPublicId(String avatarPublicId) {
        this.avatarPublicId = avatarPublicId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
