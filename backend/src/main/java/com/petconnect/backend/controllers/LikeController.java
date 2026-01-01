package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.forum.LikeDTO;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.jpa.UserRepository;
import com.petconnect.backend.services.LikeService;
import com.petconnect.backend.mappers.LikeMapper;
import com.petconnect.backend.utils.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/likes")
@Tag(name = "Likes", description = "APIs for forum and comment likes")
public class LikeController extends BaseController {

    private final LikeService likeService;
    private final LikeMapper likeMapper;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    public LikeController(LikeService likeService, LikeMapper likeMapper, UserRepository userRepository) {
        super(logger);
        this.likeService = likeService;
        this.likeMapper = likeMapper;
        this.userRepository = userRepository;
    }

    /**
     * Fetch likes for a forum by its ID.
     *
     * @param forumId the forum ID
     * @return a response containing a list of likes for the specified forum
     */
    @Operation(summary = "Get likes for a forum", description = "Fetch all likes for a specific forum")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Likes fetched successfully")
    })
    @GetMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<List<LikeDTO>>> getAllLikesByForumId(
            @Parameter(description = "Forum ID", required = true)
            @PathVariable String forumId
    ) {
        logger.debug("Fetching likes for forum ID: {}", forumId);

        // Fetch likes by forum ID and map them to DTOs
        List<LikeDTO> likes = likeService.getAllLikesByForumId(forumId).stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntityUtil.ok("Likes fetched successfully", likes);
    }

    /**
     * Toggle like on a forum.
     *
     * @param forumId     the forum ID
     * @param userDetails the authenticated user details
     * @return a response indicating whether the forum was liked or unliked
     */
    @Operation(summary = "Toggle like on a forum", description = "Like or unlike a forum")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Like toggled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<String>> toggleLikeOnForum(
            @Parameter(description = "Forum ID", required = true)
            @PathVariable String forumId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntityUtil.unauthorized("User is not authenticated");
        }

        String username = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(username);

        if (user.isEmpty()) {
            return ResponseEntityUtil.notFound("User not found");
        }

        Map<String, String> response = likeService.toggleLikeOnForum(forumId, username);
        return ResponseEntityUtil.ok(response.get("message"), (String) null);
    }

    /**
     * Fetch likes for a comment by its ID.
     *
     * @param commentId the comment ID
     * @return a response containing a list of likes for the specified comment
     */
    @Operation(summary = "Get likes for a comment", description = "Fetch all likes for a specific comment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Likes fetched successfully")
    })
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDTO<List<LikeDTO>>> getAllLikesByCommentId(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable String commentId
    ) {
        List<LikeDTO> likes = likeService.getAllLikesByCommentId(commentId).stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntityUtil.ok("Likes fetched successfully", likes);
    }

    /**
     * Toggle like on a comment.
     *
     * @param commentId the comment ID
     * @param userDetails the authenticated user's details
     * @return a response indicating whether the comment was liked or unliked
     */
    @Operation(summary = "Toggle like on a comment", description = "Like or unlike a comment")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Like toggled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDTO<String>> toggleLikeOnComment(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable String commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntityUtil.unauthorized("User is not authenticated");
        }
        String username = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(username);

        if (user.isEmpty()) {
            return ResponseEntityUtil.notFound("User not found");
        }

        Map<String, String> response = likeService.toggleLikeOnComment(commentId, username);
        return ResponseEntityUtil.ok(response.get("message"), (String) null);
    }

    @Operation(summary = "Check if user liked a forum", description = "Returns true if the authenticated user has liked the forum")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check completed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/forums/{forumId}/check")
    public ResponseEntity<ApiResponseDTO<Map<String, Boolean>>> checkIfUserLikedForum(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Forum ID", required = true)
            @PathVariable String forumId
    ) {
        if (userDetails == null) {
            return ResponseEntityUtil.unauthorized("User is not authenticated");
        }

        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        if (user.isEmpty()) {
            return ResponseEntityUtil.notFound("User not found");
        }

        boolean isLiked = likeService.checkIfUserLikedForum(user.get().getUserId(), forumId);
        String message = isLiked ? "User has liked the forum" : "User has not liked the forum";

        Map<String, Boolean> responseData = new HashMap<>();
        responseData.put("isLiked", isLiked);

        return ResponseEntityUtil.ok(message, responseData);
    }
}
