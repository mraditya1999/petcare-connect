package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.ForumDTO;
import com.petconnect.backend.entity.Forum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ForumMapper {
//    ForumMapper INSTANCE = Mappers.getMapper(ForumMapper.class);
    ForumDTO toDTO(Forum forum);
    Forum toEntity(ForumDTO forumDTO);
}



