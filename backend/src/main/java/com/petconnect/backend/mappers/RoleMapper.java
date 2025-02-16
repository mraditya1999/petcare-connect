package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.RoleDTO;
import com.petconnect.backend.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO roleDTO);
}

