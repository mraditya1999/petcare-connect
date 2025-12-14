package com.petconnect.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDTO {

    private Long userId;
    private String email;
    private List<String> roles;
    private String token;
    private String oauthProvider;
    private boolean isProfileComplete;
}
