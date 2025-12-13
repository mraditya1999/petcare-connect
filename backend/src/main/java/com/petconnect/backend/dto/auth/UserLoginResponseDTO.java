package com.petconnect.backend.dto.auth;

import java.util.List;

public class UserLoginResponseDTO {

    private Long userId;
    private String email;
    private List<String> roles;
    private String token;
    private String oauthProvider;
    private boolean isProfileComplete;

    public UserLoginResponseDTO() {}

    public UserLoginResponseDTO(String email, List<String> roles, String token, Long userId, String oauthProvider, boolean isProfileComplete) {
        this.email = email;
        this.roles = roles;
        this.token = token;
        this.userId = userId;
        this.oauthProvider = oauthProvider;
        this.isProfileComplete = isProfileComplete;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public boolean isProfileComplete() {
        return isProfileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        isProfileComplete = profileComplete;
    }
}
