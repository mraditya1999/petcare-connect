package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.forum.CommentDTO;
import com.petconnect.backend.services.CommentService;
import com.petconnect.backend.utils.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/comments")
@Tag(
        name = "Comments",
        description = "APIs for forum comments and replies"
)
public class CommentController extends BaseController {

    private final CommentService commentService;

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    public CommentController(CommentService commentService) {
        super(logger);
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
    @Operation(
            summary = "Get comments by forum ID",
            description = "Fetch all comments for a forum with pagination"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Forum not found")
    })
    @GetMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<Page<CommentDTO>>> getAllCommentsByForumId(
            @Parameter(description = "Forum ID", required = true)
            @PathVariable String forumId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size", example = "5")
            @RequestParam(defaultValue = "5") @Min(1) int size
    ) {
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
    @Operation(
            summary = "Create comment",
            description = "Create a comment on a forum post"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> createComment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Forum ID", required = true)
            @PathVariable String forumId,

            @Valid @RequestBody CommentDTO commentDTO
    ) {
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
    @Operation(
            summary = "Update comment",
            description = "Update an existing comment (only owner allowed)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found or unauthorized")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> updateComment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Comment ID", required = true)
            @PathVariable String commentId,

            @Valid @RequestBody CommentDTO commentDTO
    ) {
        String username = userDetails.getUsername();
        Optional<CommentDTO> updatedComment = commentService.updateComment(username, commentId, commentDTO);
        return updatedComment.map(dto -> ResponseEntityUtil.ok("Comment updated successfully", dto))
                .orElseGet(() -> ResponseEntityUtil.notFound("Comment not found or you are not authorized to update it"));
    }

    /**
     * Delete a comment by its ID.
     *
     * @param commentId the comment ID
     * @param userDetails the authenticated user's details
     * @return a response indicating the result of the deletion
     */
    @Operation(
            summary = "Delete comment",
            description = "Delete a comment by ID (only owner allowed)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found or unauthorized")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteComment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Comment ID", required = true)
            @PathVariable String commentId
    ) {
        String username = userDetails.getUsername();
        boolean deleted = commentService.deleteComment(username, commentId);
        if (deleted) {
            return ResponseEntityUtil.ok("Comment deleted successfully", null);
        } else {
            return ResponseEntityUtil.notFound("Comment not found or you are not authorized to delete it");
        }
    }

    @Operation(
            summary = "Get comment with replies",
            description = "Fetch a comment along with its nested replies"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> getCommentByIdWithSubcomments(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable String commentId
    ) {
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
    @Operation(
            summary = "Reply to comment",
            description = "Reply to an existing comment in a forum"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reply added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/forums/{forumId}/comments/{parentId}/replies")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> replyToComment(
            @Parameter(description = "Forum ID", required = true)
            @PathVariable String forumId,

            @Parameter(description = "Parent comment ID", required = true)
            @PathVariable String parentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Valid @RequestBody CommentDTO commentDTO
    ) {
        if (userDetails == null) {
            return ResponseEntityUtil.unauthorized("User is not authenticated");
        }
        CommentDTO reply = commentService.replyToComment(forumId, userDetails.getUsername(), commentDTO, parentId);
        return ResponseEntityUtil.ok("Reply added successfully", reply);
    }
}
