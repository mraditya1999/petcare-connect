package com.petconnect.backend.dto;

public class RoleDTO {
    private String roleName;

    public RoleDTO() {
    }

    public RoleDTO( String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
