package com.spring.petcareConnect.dtos.profile.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteProfileResponseDto {
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
