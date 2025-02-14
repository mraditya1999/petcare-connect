package com.petconnect.backend.dto;

import lombok.Builder;

//@Builder
public class SpecialistDTO {
    private Long SpecialistId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String mobileNumber;
    private String speciality;
    private String about;
    private AddressDTO address;
    private String avatarUrl;
    private String avatarPublicId;


    public SpecialistDTO() {
    }

    public SpecialistDTO(Long SpecialistId, String firstName, String lastName, String email, String password, String mobileNumber, String speciality, String about, AddressDTO address, String avatarUrl, String avatarPublicId) {
        this.SpecialistId = SpecialistId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.speciality = speciality;
        this.about = about;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
    }

    public Long getSpecialistId() {
        return SpecialistId;
    }

    public void setSpecialistId(Long SpecialistId) {
        this.SpecialistId = SpecialistId;
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
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
