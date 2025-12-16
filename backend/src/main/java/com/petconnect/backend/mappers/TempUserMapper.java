package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.auth.TempUserDTO;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TempUserMapper {

    @Mapping(target ="password", source = "password")
    TempUserDTO toTempUserDTO(User user);

    @Mapping(target ="password", source = "password")
    TempUserDTO toTempUserDTO(Specialist specialist);

    @Mapping(target ="userId", ignore = true)
    @Mapping(target ="roles", ignore = true)
    @Mapping(target ="address", ignore = true)
    @Mapping(target ="avatarUrl", ignore = true)
    @Mapping(target ="avatarPublicId", ignore = true)
    @Mapping(target ="oauthAccounts", ignore = true)
    @Mapping(target ="isVerified", ignore = true)
    @Mapping(target ="verificationToken", ignore = true)
    User toEntity(TempUserDTO dto);
}
