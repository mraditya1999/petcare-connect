package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.forum.request.CommentCreateRequestDto;
import com.spring.petcareConnect.dtos.forum.request.CommentUpdateRequestDto;
import com.spring.petcareConnect.dtos.forum.response.CommentListResponseDto;
import com.spring.petcareConnect.dtos.forum.response.CommentResponseDto;
import com.spring.petcareConnect.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forums/{forumId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<CommentResponseDto>> addComment( @PathVariable String forumId, @Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto) {
        CommentResponseDto commentResponseDto = commentService.addComment(forumId,commentCreateRequestDto);
        CustomApiResponse<CommentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.COMMENT_CREATED, commentResponseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CustomApiResponse<CommentResponseDto>> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody CommentUpdateRequestDto dto) {
        CommentResponseDto updatedComment = commentService.updateCommentForUser(commentId, dto);
        CustomApiResponse<CommentResponseDto> response =
                new CustomApiResponse<>(true, ResponseMessages.COMMENT_UPDATED, updatedComment);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<CustomApiResponse<CommentListResponseDto>> getCommentsByForum(@PathVariable String forumId, @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                                    @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false)   Integer pageSize,
                                                                                    @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_FORUM_BY, required = false) String sortBy,
                                                                                    @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        CommentListResponseDto commentListResponseDto = commentService.getCommentsByForum(forumId,pageNumber, pageSize, sortBy, sortOrder);
        CustomApiResponse<CommentListResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.COMMENTS_FETCHED, commentListResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CustomApiResponse<String>> deleteComment(@PathVariable String commentId) {
        commentService.deleteCommentForUser(commentId);
        CustomApiResponse<String> response = new CustomApiResponse<>(true, ResponseMessages.COMMENT_DELETED, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
