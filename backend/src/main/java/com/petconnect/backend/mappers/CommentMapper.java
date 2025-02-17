package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.CommentDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.CommentRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;


    @Mapping(target = "parentId", source = "parentComment.commentId")
    @Mapping(target = "firstName", source = "userId", qualifiedByName = "mapUserFirstName")
    @Mapping(target = "lastName", source = "userId", qualifiedByName = "mapUserLastName")
    @Mapping(target = "email", source = "userId", qualifiedByName = "mapUserEmail")
    @Mapping(target = "replies", ignore = true)
    public abstract CommentDTO toDTO(Comment comment);

    @Mapping(target = "parentComment", source = "parentId", qualifiedByName = "mapParentComment")
    @Mapping(target = "replies", ignore = true)
    public abstract Comment toEntity(CommentDTO commentDTO);

    @Named("mapParentComment")
    protected Comment mapParentComment(String parentId) {
        if (parentId == null) {
            return null;
        }
        return commentRepository.findById(parentId).orElse(null);
    }

    @Named("mapUserFirstName")
    protected String mapUserFirstName(Long userId) {
        return userRepository.findById(userId).map(User::getFirstName).orElse(null);
    }

    @Named("mapUserLastName")
    protected String mapUserLastName(Long userId) {
        return userRepository.findById(userId).map(User::getLastName).orElse(null);
    }

    @Named("mapUserEmail")
    protected String mapUserEmail(Long userId) {
        return userRepository.findById(userId).map(User::getEmail).orElse(null);
    }

    @Named("mapRepliesToDTO")
    protected Set<CommentDTO> mapRepliesToDTO(Set<Comment> replies) {
        if (replies == null) {
            return null;
        }
        return replies.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

    @Named("mapRepliesToEntity")
    protected Set<Comment> mapRepliesToEntity(Set<CommentDTO> replies) {
        if (replies == null) {
            return null;
        }
        return replies.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}
