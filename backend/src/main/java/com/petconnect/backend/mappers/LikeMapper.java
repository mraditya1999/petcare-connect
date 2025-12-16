package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.forum.LikeDTO;
import com.petconnect.backend.entity.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    LikeDTO toDTO(Like like);

    Like toEntity(LikeDTO likeDTO);
}

