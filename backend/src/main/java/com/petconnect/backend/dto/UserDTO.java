package com.petconnect.backend.dto;

import com.petconnect.backend.entity.Address;

import java.util.Set;

public class UserDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDTO address;

    public UserDTO() {}

    public UserDTO(Long userId, String firstName, String lastName, String email,  AddressDTO address) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}
