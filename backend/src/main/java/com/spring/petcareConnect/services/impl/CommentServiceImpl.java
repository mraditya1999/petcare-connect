package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.forum.request.CommentCreateRequestDto;
import com.spring.petcareConnect.dtos.forum.request.CommentUpdateRequestDto;
import com.spring.petcareConnect.dtos.forum.response.CommentListResponseDto;
import com.spring.petcareConnect.dtos.forum.response.CommentResponseDto;
import com.spring.petcareConnect.entities.Comment;
import com.spring.petcareConnect.entities.Forum;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.exceptions.ResourceNotFoundException;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.repositories.mongo.CommentRepository;
import com.spring.petcareConnect.repositories.mongo.ForumRepository;
import com.spring.petcareConnect.services.CommentService;
import com.spring.petcareConnect.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    public CommentServiceImpl(UserRepository userRepository, ForumRepository forumRepository, CommentRepository commentRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CommentResponseDto addComment(String forumId, CommentCreateRequestDto dto) {
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> new APIException("No logged-in user"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", forumId));

        Comment comment = new Comment();
        comment.setForumId(forum.getForumId());
        comment.setUserId(user.getUserId());
        comment.setText(dto.getText());
        comment.setParentId(dto.getParentId());
        comment.setCreatedAt(Instant.now());

        comment = commentRepository.save(comment);

        forum.setCommentCount(forum.getCommentCount() + 1);
        forumRepository.save(forum);

        return convertToCommentDTO(comment);
    }

    @Override
    public CommentResponseDto updateCommentForUser(String commentId, CommentUpdateRequestDto dto) {
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> new APIException("No logged-in user"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getUserId().equals(user.getUserId())) {
            throw new APIException("You cannot edit someone else’s comment");
        }

        comment.setText(dto.getText().trim());
        comment.setIsEdited(true);
        comment.setUpdatedAt(Instant.now());
        comment = commentRepository.save(comment);

        return convertToCommentDTO(comment);
    }


    @Override
    public CommentListResponseDto getCommentsByForum(String forumId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Comment> commentPage = commentRepository.findAllByForumId(forumId, pageable);

        if (commentPage.isEmpty()) {
            return new CommentListResponseDto(List.of(), pageNumber, pageSize, 0L, 1, true);
        }

        List<CommentResponseDto> content = commentPage.getContent().stream()
                .map(this::convertToCommentDTO)
                .toList();

        return new CommentListResponseDto(content, commentPage.getNumber(), commentPage.getSize(), commentPage.getTotalElements(), commentPage.getTotalPages(), commentPage.isLast());
    }

    @Override
    public void deleteCommentForUser(String commentId) {
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> new APIException("No logged-in user"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getUserId().equals(user.getUserId())) {
            throw new APIException("You cannot delete someone else’s comment");
        }

        commentRepository.delete(comment);

        Forum forum = forumRepository.findById(comment.getForumId())
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", comment.getForumId()));
        forum.setCommentCount(forum.getCommentCount() - 1);
        forumRepository.save(forum);
    }

    private CommentResponseDto convertToCommentDTO(Comment comment) {
        CommentResponseDto dto = modelMapper.map(comment, CommentResponseDto.class);
        return dto;
    }

    private Pageable buildPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }
}
