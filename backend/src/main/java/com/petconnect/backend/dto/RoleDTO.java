package com.petconnect.backend.dto;

public class RoleDTO {
    private Integer roleId;
    private String roleName;

    // Default constructor
    public RoleDTO() {
    }

    // Parameterized constructor
    public RoleDTO(Integer roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // Getters and setters
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
