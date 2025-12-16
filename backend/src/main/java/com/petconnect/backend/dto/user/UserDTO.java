package com.petconnect.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDTO address;
    private String avatarUrl;
    private String avatarPublicId;
    private String mobileNumber;

    // All-args constructor
    public UserDTO(Long userId, String firstName, String lastName, String email, AddressDTO address, String avatarUrl, String avatarPublicId, String mobileNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
        this.mobileNumber = mobileNumber;
    }

    // Convenience constructor
    public UserDTO(String firstName, String lastName, String mobileNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
    }
}
