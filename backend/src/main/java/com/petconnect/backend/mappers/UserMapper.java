package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.dto.UserRegistrationRequest;
import com.petconnect.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

//@Mapper(componentModel = "spring", uses = { AddressMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
//public interface UserMapper {
//
//    @Mapping(target = "address", source = "address")
//    UserDTO toDTO(User user);
//
//    @Mapping(target = "address", source = "address")
//    User toEntity(UserDTO userDTO);
//
//    @Mapping(target = "address", ignore = true) // Assuming registration doesn't include address
//    User toEntity(UserRegistrationRequest userRegistrationRequest);
//}


@Mapper(componentModel = "spring", uses = { AddressMapper.class, RoleMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mapping(target = "address", source = "address")
    @Mapping(target = "roles", source = "roles")
    UserDTO toDTO(User user);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "roles", source = "roles")
    User toEntity(UserDTO userDTO);

    @Mapping(target = "address", ignore = true) // Assuming registration doesn't include address
    User toEntity(UserRegistrationRequest userRegistrationRequest);
}
