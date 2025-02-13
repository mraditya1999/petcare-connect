package com.petconnect.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class SpecialistUpdateRequestDTO {
    @Size(max = 255, message = "First name cannot exceed 255 characters")
    private String firstName;

    @Size(max = 255, message = "Last name cannot exceed 255 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 20, message = "Mobile number cannot exceed 20 characters")
    private String mobileNumber;

    @Size(max = 255, message = "Speciality cannot exceed 255 characters")
    private String speciality;

    @Size(max = 500, message = "About cannot exceed 500 characters")
    private String about;

    private Long pincode;

    @Size(max = 255, message = "City cannot exceed 255 characters")
    private String city;

    @Size(max = 255, message = "State cannot exceed 255 characters")
    private String state;

    private String locality;

    @Size(max = 255, message = "Country cannot exceed 255 characters")
    private String country;

    private String avatarUrl;
    private String avatarPublicId;

    public SpecialistUpdateRequestDTO() {
    }

    public SpecialistUpdateRequestDTO(String firstName, String lastName, String email, String password, String mobileNumber, String speciality, String about, Long pincode, String city, String state, String locality, String country, String avatarUrl, String avatarPublicId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.speciality = speciality;
        this.about = about;
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.locality = locality;
        this.country = country;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Long getPincode() {
        return pincode;
    }

    public void setPincode(Long pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
}

