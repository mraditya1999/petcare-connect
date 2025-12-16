package com.petconnect.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteProfileResponseDTO {
    private String email;
    private List<String> roles;
    private String token;
    private Long userId;
    private String oauthProvider;
    private boolean isNewUser;

    public String getJwt() {
        return token;
    }

    public void setJwt(String token) {
        this.token = token;
    }
}
