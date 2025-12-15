package com.petconnect.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempUserDTO {
    private String email;
    private String password;
    private String otp;
    private boolean verified;
    private Date createdAt;
    private Date updatedAt;
    private String userRole;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String resetToken;
    private boolean isProfileComplete;
}
