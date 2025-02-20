package com.petconnect.backend.dto.specialist;//package com.petconnect.backend.dto;

import com.petconnect.backend.dto.user.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class SpecialistCreateRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(max = 255, message = "First name cannot exceed 255 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 255, message = "Last name cannot exceed 255 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Mobile number is required")
    @Size(max = 20, message = "Mobile number cannot exceed 20 characters")
    private String mobileNumber;

    @NotBlank(message = "Speciality is required")
    @Size(max = 255, message = "Speciality cannot exceed 255 characters")
    private String speciality;

    @NotBlank(message = "About is required")
    @Size(max = 500, message = "About cannot exceed 500 characters")
    private String about;


    @Min(value = 100000, message = "Pincode must be at least 6 digits")
    private Long pincode;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @Size(max = 255, message = "Locality cannot exceed 255 characters")
    private String locality;

    public SpecialistCreateRequestDTO() {
    }

    public SpecialistCreateRequestDTO(String firstName, String lastName, String email, String password, String mobileNumber, String speciality, String about, Long pincode, String city, String state, String country, String locality) {
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
        this.country = country;
        this.locality = locality;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}

