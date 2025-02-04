package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.LikeDTO;
import com.petconnect.backend.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    LikeDTO toDTO(Like like);

    Like toEntity(LikeDTO likeDTO);
}

