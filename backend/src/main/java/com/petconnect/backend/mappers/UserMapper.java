package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.dto.auth.UserRegistrationRequestDTO;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { AddressMapper.class, RoleMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mapping(target = "address", source = "address")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "toRoleNames")
    UserDTO toDTO(User user);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "toRoles")
    User toEntity(UserDTO userDTO);

    @Mapping(target = "address", ignore = true) // Assuming registration doesn't include address
    User toEntity(UserRegistrationRequestDTO userRegistrationRequestDTO);

    @Named("toRoleNames")
    default List<Role.RoleName> toRoleNames(Set<Role> roles) {
        return roles.stream().map(Role::getRoleName).collect(Collectors.toList());
    }

    @Named("toRoles")
    default Set<Role> toRoles(List<Role.RoleName> roleNames) {
        return roleNames.stream().map(roleName -> {
            Role role = new Role();
            role.setRoleName(roleName);
            return role;
        }).collect(Collectors.toSet());
    }
}
