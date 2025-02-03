package com.petconnect.backend.dto;

import java.util.List;

public class UserLoginResponse {

    private String email;
    private List<String> roles;
    private String token;

    public UserLoginResponse() {
    }

    public UserLoginResponse( String email, List<String> roles, String token) {
        this.email = email;
        this.roles = roles;
        this.token = token;
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
