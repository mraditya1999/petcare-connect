package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.dto.auth.UserRegistrationRequestDTO;
import com.petconnect.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", uses = { AddressMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mapping(target = "address", source = "address")
    UserDTO toDTO(User user);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "mobileNumber", source = "mobileNumber")
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "avatarPublicId", source = "avatarPublicId")
    User toEntity(UserDTO userDTO);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    User toEntity(UserRegistrationRequestDTO userRegistrationRequestDTO);
}
