package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { AddressMapper.class, RoleMapper.class })
public interface SpecialistMapper {

    @Mapping(target = "specialistId", source = "userId")
    SpecialistDTO toDTO(Specialist specialist);

    @Mapping(target = "address", source = "addressDTO")
    Specialist toSpecialistEntity(SpecialistCreateRequestDTO specialistCreateRequestDTO);

    @Mapping(target = "userId", source = "specialistId")
    SpecialistResponseDTO toSpecialistResponseDTO(Specialist specialist);

    @Mapping(target = "userId", source = "specialistId")
    SpecialistResponseDTO toSpecialistResponseDTO(SpecialistDTO specialistDTO);

    @Mapping(target = "address", source = "addressDTO")
    void updateSpecialistEntity(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, @MappingTarget Specialist specialist);

    // Custom mapping for roles to handle enum conversion
    default List<Role.RoleName> toRoleNames(Set<Role> roles) {
        return roles.stream().map(Role::getRoleName).collect(Collectors.toList());
    }

    default Set<Role> toRoles(List<Role.RoleName> roleNames) {
        return roleNames.stream().map(roleName -> {
            Role role = new Role();
            role.setRoleName(roleName);
            return role;
        }).collect(Collectors.toSet());
    }
}
