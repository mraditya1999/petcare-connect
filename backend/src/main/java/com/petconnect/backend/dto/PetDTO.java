package com.petconnect.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PetDTO {

    private Long petId;

    @NotNull
    @Size(min = 1, max = 255)
    private String petName;

    @NotNull
    @Positive
    private Integer age;

    @NotNull
    @Positive
    private Double weight;

    private String avatarUrl;

    @NotNull
    @Size(min = 1, max = 10)
    private String gender;

    @NotNull
    @Size(min = 1, max = 255)
    private String breed;

    @NotNull
    @Size(min = 1, max = 255)
    private String species;

    public PetDTO() {
    }

    public PetDTO(String petName, String breed, Integer age, Double weight, String gender, String species) {
        this.petName = petName;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
        this.species = species;
    }

    public PetDTO(Long petId, String petName, Integer age, Double weight, String avatarUrl, String gender, String breed, String species) {
        this.petId = petId;
        this.petName = petName;
        this.age = age;
        this.weight = weight;
        this.avatarUrl = avatarUrl;
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
