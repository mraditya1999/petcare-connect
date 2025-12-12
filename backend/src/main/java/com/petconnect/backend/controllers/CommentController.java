package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.CommentDTO;
import com.petconnect.backend.services.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Get all comments by forum ID with pagination.
     *
     * @param forumId the forum ID
     * @param page the page number to retrieve (default is 0)
     * @param size the number of items per page (default is 5)
     * @return ResponseEntity with ApiResponse containing a page of comments
     */
    @GetMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<Page<CommentDTO>>> getAllCommentsByForumId(
            @PathVariable String forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        logger.debug("Fetching comments for forum ID: {}", forumId);
        Page<CommentDTO> comments = commentService.getAllCommentsByForumId(forumId, page, size);
        return ResponseEntity.ok(new ApiResponseDTO<>("Comments fetched successfully", comments));
    }

    /**
     * Create a comment on a forum post.
     *
     * @param forumId the forum ID
     * @param userDetails the authenticated user's details
     * @param commentDTO the comment data
     * @return the created comment
     */
    @PostMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String forumId,
            @Valid @RequestBody CommentDTO commentDTO) {
        String username = userDetails.getUsername();
        CommentDTO comment = commentService.createComment(username, forumId, commentDTO);
        ApiResponseDTO<CommentDTO> apiResponseDTO = new ApiResponseDTO<>("Comment added successfully", comment);
        return ResponseEntity.ok(apiResponseDTO);
    }

    /**
     * Update a comment on a forum post.
     *
     * @param commentId the comment ID
     * @param userDetails the authenticated user's details
     * @param commentDTO the updated comment data
     * @return the updated comment
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> updateComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String commentId, @Valid @RequestBody CommentDTO commentDTO) {
        String username = userDetails.getUsername();
        Optional<CommentDTO> updatedComment = commentService.updateComment(username,commentId, commentDTO);
        if (updatedComment.isPresent()) {
            ApiResponseDTO<CommentDTO> apiResponseDTO = new ApiResponseDTO<>("Comment updated successfully", updatedComment.get());
            return ResponseEntity.ok(apiResponseDTO);
        } else {
            ApiResponseDTO<CommentDTO> apiResponseDTO = new ApiResponseDTO<>("Comment not found or you are not authorized to update it", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
        }
    }


    /**
     * Delete a comment by its ID.
     *
     * @param commentId the comment ID
     * @param userDetails the authenticated user's details
     * @return a response indicating the result of the deletion
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String commentId) {
        String username = userDetails.getUsername();
        boolean deleted = commentService.deleteComment(username,commentId);
        if (deleted) {
            ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>("Comment deleted successfully", null);
            return ResponseEntity.ok(apiResponseDTO);
        } else {
            ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>("Comment not found or you are not authorized to delete it", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentByIdWithSubcomments(@PathVariable String commentId) {
        CommentDTO comment = commentService.getCommentByIdWithSubcomments(commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    /**
     * Reply to a comment on a forum post.
     *
     * @param forumId the forum ID
     * @param parentId the parent comment ID
     * @param userDetails the authenticated user's details
     * @param commentDTO the reply comment data
     * @return the created reply comment
     */
    @PostMapping("/forums/{forumId}/comments/{parentId}/replies")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> replyToComment(
            @PathVariable String forumId,
            @PathVariable String parentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentDTO commentDTO) {

        if (userDetails == null) {
            ApiResponseDTO<CommentDTO> apiResponseDTO = new ApiResponseDTO<>("User is not authenticated", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        }

        CommentDTO reply = commentService.replyToComment(forumId, userDetails.getUsername(), commentDTO, parentId);
        ApiResponseDTO<CommentDTO> apiResponseDTO = new ApiResponseDTO<>("Reply added successfully", reply);
        return ResponseEntity.ok(apiResponseDTO);
    }
}


