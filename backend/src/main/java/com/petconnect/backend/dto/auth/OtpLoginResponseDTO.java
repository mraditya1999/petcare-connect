package com.petconnect.backend.dto.auth;

import java.util.List;

public class OtpLoginResponseDTO {
    private String email;
    private List<String> roles;
    private String token;
    private Long userId;
    private String oauthProvider;
    private boolean isNewUser;

    public OtpLoginResponseDTO() {
    }

    public OtpLoginResponseDTO(String email, List<String> roles, String token, Long userId, String oauthProvider, boolean isNewUser) {
        this.email = email;
        this.roles = roles;
        this.token = token;
        this.userId = userId;
        this.oauthProvider = oauthProvider;
        this.isNewUser = isNewUser;
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

    public String getJwt() {
        return token;
    }

    public void setJwt(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }
}
