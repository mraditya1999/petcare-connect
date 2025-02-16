package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.CommentDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.services.CommentService;
import com.petconnect.backend.mappers.CommentMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getAllComments() {
        logger.debug("Fetching all comments");
        List<CommentDTO> comments = commentService.getAllComments().stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Comments fetched successfully", comments));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentById(@PathVariable String commentId) {
        logger.debug("Fetching comment with ID: {}", commentId);
        Optional<Comment> comment = commentService.getCommentById(commentId);
        return comment.map(value -> ResponseEntity.ok(new ApiResponse<>("Comment fetched successfully", commentMapper.toDTO(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Comment not found", null)));
    }

    @GetMapping("/forum/{forumId}")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByForumId(@PathVariable String forumId) {
        logger.debug("Fetching comments for forum ID: {}", forumId);
        List<CommentDTO> comments = commentService.getAllCommentsByForumId(forumId).stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Comments fetched successfully", comments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDTO>> createComment(@Valid @RequestBody CommentDTO commentDTO) {
        logger.debug("Creating new comment");
        Comment comment = commentMapper.toEntity(commentDTO);
        Comment savedComment = commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Comment created successfully", commentMapper.toDTO(savedComment)));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> updateCommentById(@PathVariable String commentId, @Valid @RequestBody CommentDTO commentDTO) {
        logger.debug("Updating comment with ID: {}", commentId);
        Optional<Comment> updatedComment = commentService.updateCommentById(commentId, commentMapper.toEntity(commentDTO));
        return updatedComment.map(value -> ResponseEntity.ok(new ApiResponse<>("Comment updated successfully", commentMapper.toDTO(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Comment not found", null)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteCommentById(@PathVariable String commentId) {
        logger.debug("Deleting comment with ID: {}", commentId);
        commentService.deleteCommentById(commentId);
        return ResponseEntity.ok(new ApiResponse<>("Comment deleted successfully", null));
    }

    @PostMapping("/{commentId}/like")
    public void likeComment(@PathVariable String commentId) {
        commentService.likeComment(commentId);
    }

    @PostMapping("/{commentId}/unlike")
    public void unlikeComment(@PathVariable String commentId) {
        commentService.unlikeComment(commentId);
    }
}
