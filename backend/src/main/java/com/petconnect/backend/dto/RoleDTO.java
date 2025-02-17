package com.petconnect.backend.dto;

import com.petconnect.backend.entity.Role;

public class RoleDTO {
    private Role.RoleName roleName;

    public RoleDTO() {
    }

    public RoleDTO(Role.RoleName roleName) {
        this.roleName = roleName;
    }

    public Role.RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(Role.RoleName roleName) {
        this.roleName = roleName;
    }
}
