package com.petconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "specialists")
@PrimaryKeyJoinColumn(name = "specialist_id")
@SuperBuilder
public class Specialist extends User {

    @Column(nullable = false, length = 500)
    private String about;

    @Column(nullable = false, length = 100)
    private String speciality;

    public Specialist() {
    }

    public Specialist(Long userId, String firstName, String lastName, String email, Address address, String avatarUrl, String avatarPublicId, String mobileNumber, String password, String verificationToken, String resetToken, Set<Role> roles, boolean isVerified, Date createdAt, Date updatedAt, List<Pet> pets, String about, String speciality) {
        super(userId, firstName, lastName, email, address, avatarUrl, avatarPublicId, mobileNumber, password, verificationToken, resetToken, roles, isVerified, createdAt, updatedAt, pets);
        this.about = about;
        this.speciality = speciality;
    }

    public Long getSpecialistId() {
        return getUserId();
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @Override
    public String toString() {
        return "Specialist{" +
                "userId=" + getUserId() +  // Inherited userId
                ", firstName=" + getFirstName() +  // Inherited field
                ", lastName=" + getLastName() +
                ", email=" + getEmail() +
                ", about='" + about + '\'' +
                ", speciality='" + speciality + '\'' +
                '}';
    }
}
