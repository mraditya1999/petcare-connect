package com.petconnect.backend.dto;

public class SpecialistResponseDTO {
    private Long specialistId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String speciality;
    private String about;
    private String avatarUrl;
    private AddressDTO address;

    public SpecialistResponseDTO() {
    }

    public SpecialistResponseDTO(Long specialistId, String firstName, String lastName, String email, String mobileNumber, String speciality, String about, String avatarUrl, AddressDTO address) {
        this.specialistId = specialistId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.speciality = speciality;
        this.about = about;
        this.avatarUrl = avatarUrl;
        this.address = address;
    }

    public Long getSpecialistId() {
        return specialistId ;
    }

    public void setSpecialistId(Long specialistId ) {
        this.specialistId    = specialistId ;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}