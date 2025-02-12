package com.petconnect.backend.dto;

import java.time.LocalDateTime;

public class PetDTO {

    private Long petId;
    private String petName;
    private Integer age;
    private Double weight;
    private String avatarPublicId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerMobileNumber;
    private LocalDateTime createdAt;  // Add this
    private LocalDateTime updatedAt;  // Add this

    public PetDTO() {
    }

    public PetDTO(Long petId, String petName, Integer age, Double weight, String avatarPublicId, String ownerFirstName, String ownerLastName, String ownerMobileNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.petId = petId;
        this.petName = petName;
        this.age = age;
        this.weight = weight;
        this.avatarPublicId = avatarPublicId;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerMobileNumber = ownerMobileNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getAvatarPublicId() {
        return avatarPublicId;
    }

    public void setAvatarPublicId(String avatarPublicId) {
        this.avatarPublicId = avatarPublicId;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerMobileNumber() {
        return ownerMobileNumber;
    }

    public void setOwnerMobileNumber(String ownerMobileNumber) {
        this.ownerMobileNumber = ownerMobileNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
