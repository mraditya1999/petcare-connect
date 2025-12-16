package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.CommentDTO;
import com.petconnect.backend.services.CommentService;
import com.petconnect.backend.utils.ResponseEntityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        logger.debug("Fetching comments for forum ID: {}", forumId);
        Page<CommentDTO> comments = commentService.getAllCommentsByForumId(forumId, page, size);
        return ResponseEntityUtil.page(comments, "Comments fetched successfully");
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
        return ResponseEntityUtil.ok("Comment added successfully", comment);
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
        Optional<CommentDTO> updatedComment = commentService.updateComment(username, commentId, commentDTO);
        if (updatedComment.isPresent()) {
            return ResponseEntityUtil.ok("Comment updated successfully", updatedComment.get());
        } else {
            return ResponseEntityUtil.notFound("Comment not found or you are not authorized to update it");
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
        boolean deleted = commentService.deleteComment(username, commentId);
        if (deleted) {
            return ResponseEntityUtil.ok("Comment deleted successfully", null);
        } else {
            return ResponseEntityUtil.notFound("Comment not found or you are not authorized to delete it");
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> getCommentByIdWithSubcomments(@PathVariable String commentId) {
        logger.debug("Fetching comment with subcomments for ID: {}", commentId);
        CommentDTO comment = commentService.getCommentByIdWithSubcomments(commentId);
        return ResponseEntityUtil.ok("Comment fetched successfully", comment);
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
            return ResponseEntityUtil.unauthorized("User is not authenticated");
        }

        CommentDTO reply = commentService.replyToComment(forumId, userDetails.getUsername(), commentDTO, parentId);
        return ResponseEntityUtil.ok("Reply added successfully", reply);
    }
}
