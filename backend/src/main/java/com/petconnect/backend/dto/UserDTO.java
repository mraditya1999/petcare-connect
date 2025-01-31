package com.petconnect.backend.dto;

import java.util.Set;

public class UserDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Set<RoleDTO> roles;
    private boolean isVerified;
    private boolean isTwoFactorEnabled;

    // Default constructor
    public UserDTO() {}

    // Parameterized constructor
    public UserDTO(Long userId, String firstName, String lastName, String email, Set<RoleDTO> roles, boolean isVerified, boolean isTwoFactorEnabled) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }
}
