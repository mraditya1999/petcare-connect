package com.petconnect.backend.dto.pet;

import jakarta.validation.constraints.*;

public class PetRequestDTO {

    @NotNull(message = "Pet name cannot be null")
    @Size(min = 2, max = 50, message = "Pet name must be between 2 and 50 characters")
    private String petName;

    @NotNull(message = "Breed cannot be null")
    @Size(min = 1, max = 50, message = "Breed must be between 1 and 50 characters")
    private String breed;

    @Min(value = 0, message = "Age cannot be less than 0")
    @Max(value = 30, message = "Age cannot exceed 30 years")
    private int age;

    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
    @DecimalMax(value = "300", message = "Weight cannot exceed 300 kg")
    private Double weight;

    @NotNull(message = "Gender cannot be null")
    @Pattern(regexp = "MALE|FEMALE", message = "Gender must be either 'MALE' or 'FEMALE'")
    private String gender;

    @NotNull(message = "Species cannot be null")
    @Size(min = 1, max = 50, message = "Species must be between 1 and 50 characters")
    private String species;

    private String avatarUrl;
    private String avatarPublicId;

    public PetRequestDTO() {
    }

    public PetRequestDTO(String petName, String breed, int age, Double weight, String gender, String species) {
        this.petName = petName;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
        this.species = species;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
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

    @Override
    public String toString() {
        return "PetRequestDTO{" +
                "petName='" + petName + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", gender='" + gender + '\'' +
                ", species='" + species + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", avatarPublicId='" + avatarPublicId + '\'' +
                '}';
    }
}
