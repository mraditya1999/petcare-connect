package com.petconnect.backend.dto;

import java.util.List;

public class UserLoginResponse {

    private Long userId;
    private String email;
    private List<String> roles;
    private String token;

    public UserLoginResponse() {
    }

    public UserLoginResponse(String email, List<String> roles, String token,Long userId) {
        this.email = email;
        this.roles = roles;
        this.token = token;
        this.userId = userId;
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
}
