package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", uses = { AddressMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mapping(target = "address", source = "address")
    UserDTO toDTO(User user);

    @Mapping(target = "address", source = "address")
    User toEntity(UserDTO userDTO);
}
