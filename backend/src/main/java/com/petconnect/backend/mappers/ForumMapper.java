package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.forum.ForumDTO;
import com.petconnect.backend.entity.Forum;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {CommentMapper.class, LikeMapper.class})
public interface ForumMapper {
    ForumDTO toDTO(Forum forum);

    Forum toEntity(ForumDTO forumDTO);
}
