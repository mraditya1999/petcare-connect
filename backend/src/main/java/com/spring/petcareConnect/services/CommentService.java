package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.forum.request.CommentCreateRequestDto;
import com.spring.petcareConnect.dtos.forum.request.CommentUpdateRequestDto;
import com.spring.petcareConnect.dtos.forum.response.CommentListResponseDto;
import com.spring.petcareConnect.dtos.forum.response.CommentResponseDto;
import jakarta.validation.Valid;

public interface CommentService {
    CommentResponseDto addComment(String forumId, @Valid CommentCreateRequestDto commentCreateRequestDto);

    CommentResponseDto updateCommentForUser(String commentId, CommentUpdateRequestDto dto);

    CommentListResponseDto getCommentsByForum(String forumId, Integer pageNumber, Integer pageSize,String sortBy,String sortOrder);

    void deleteCommentForUser(String commentId);
}
