package com.petconnect.backend.dto;

public class AddressDTO {
    private Long addressId;
    private Long pincode;
    private String city;
    private String state;
    private String country;
    private String locality;

    // Default constructor
    public AddressDTO() {}

    // Parameterized constructor
    public AddressDTO(Long pincode, String city, String state, String country, String locality) {
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.country = country;
        this.locality = locality;
    }

    // Getters and setters
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
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
