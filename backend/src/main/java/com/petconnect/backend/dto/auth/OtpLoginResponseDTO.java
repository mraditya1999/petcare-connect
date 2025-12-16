package com.petconnect.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpLoginResponseDTO {
    private String email;
    private List<String> roles;
    private String token;
    private Long userId;
    private String oauthProvider;
    private boolean isNewUser;

    // Custom getter/setter for JWT token (alias)
    public String getJwt() {
        return token;
    }

    public void setJwt(String token) {
        this.token = token;
    }
}
