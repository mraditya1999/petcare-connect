package com.petconnect.backend.mappers;

import com.petconnect.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Named("toRoleName")
    default Role.RoleName toRoleName(Role role) {
        return role.getRoleName();
    }

    @Named("toRole")
    default Role toRole(Role.RoleName roleName) {
        Role role = new Role();
        role.setRoleName(roleName);
        return role;
    }
}
