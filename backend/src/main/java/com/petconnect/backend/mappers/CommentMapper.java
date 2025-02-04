    package com.petconnect.backend.mappers;

    import com.petconnect.backend.dto.CommentDTO;
    import com.petconnect.backend.entity.Comment;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;

    @Mapper(componentModel = "spring")
    public interface CommentMapper {
        CommentDTO toDTO(Comment comment);

        Comment toEntity(CommentDTO commentDTO);
    }
