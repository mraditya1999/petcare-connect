package com.petconnect.backend.dto;

import com.petconnect.backend.entity.Address;
import lombok.Builder;

@Builder
public class SpecialistDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String speciality;
    private String about;
    private String password; // Assuming password is included in the DTO
    private Address address; // Assuming address is included in the DTO

    public SpecialistDTO() {
    }

    public SpecialistDTO(Long id, String firstName, String lastName, String email, String speciality, String about, String password, Address address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.speciality = speciality;
        this.about = about;
        this.password = password;
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setSpeciality(String specialty) {
        this.speciality = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
