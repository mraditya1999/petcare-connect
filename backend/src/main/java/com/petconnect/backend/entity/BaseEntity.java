//package com.petconnect.backend.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.util.Date;
//
///**
// * Entity representing a Pet.
// */
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Pet {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long petId;
//
//    @NotNull(message = "Pet name cannot be null")
//    @Size(min = 2, max = 50, message = "Pet name must be between 2 and 50 characters")
//    @Column(nullable = false)
//    private String petName;
//
//    @Min(value = 0, message = "Age cannot be less than 0")
//    @Max(value = 30, message = "Age cannot exceed 30 years")
//    private Integer age;
//
//    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1 kg")
//    @DecimalMax(value = "300", message = "Weight cannot exceed 300 kg")
//    private Double weight;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
//    @JsonIgnoreProperties("pets") // Use the same reference name as in User.java
//    private User petOwner;
//
//    @CreationTimestamp
//    @Column(nullable = false, updatable = false)
//    private Date createdAt;
//
//    @UpdateTimestamp
//    @Column(nullable = false)
//    private Date updatedAt;
//
//    @Override
//    public String toString() {
//        return "Pet{" +
//                "petId=" + petId +
//                ", petName='" + petName + '\'' +
//                ", age=" + age +
//                ", weight=" + weight +
//                ", petOwner=" + petOwner.getUserId() + // Avoid recursive call to petOwner
//                '}';
//    }
//}
