package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * Entity representing a Pet.
 */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@Entity
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    @JsonBackReference
    private User owner;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

    @Column
    private String avatarUrl; // Add this

    @Column
    private String avatarPublicId; // Add this

    public Pet() {
    }

    public Pet(Long petId, String petName, Integer age, Double weight, User owner, Date createdAt, Date updatedAt, String avatarUrl, String avatarPublicId) {
        this.petId = petId;
        this.petName = petName;
        this.age = age;
        this.weight = weight;
        this.owner = owner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
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

    public User getPetOwner() {
        return owner;
    }

    public void setPetOwner(User owner) {
        this.owner = owner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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
        return "Pet{" +
                "petId=" + petId +
                ", petName='" + petName + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", petOwner=" + owner.getUserId() + // Avoid recursive call to petOwner
                ", avatarUrl='" + avatarUrl + '\'' +
                ", avatarPublicId='" + avatarPublicId + '\'' +
                '}';
    }
}
