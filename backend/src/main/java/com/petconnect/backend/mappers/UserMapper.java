package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
}
