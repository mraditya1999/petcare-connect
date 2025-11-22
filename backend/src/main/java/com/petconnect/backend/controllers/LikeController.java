package com.petconnect.backend.controllers;

import com.cloudinary.api.ApiResponse;
import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.LikeDTO;
import com.petconnect.backend.entity.Like;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.LikeService;
import com.petconnect.backend.mappers.LikeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    private final LikeService likeService;
    private final LikeMapper likeMapper;
    private final UserRepository userRepository;

    @Autowired
    public LikeController(LikeService likeService, LikeMapper likeMapper, UserRepository userRepository) {
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
    @GetMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<List<LikeDTO>>> getAllLikesByForumId(@PathVariable String forumId) {
        logger.debug("Fetching likes for forum ID: {}", forumId);

        // Fetch likes by forum ID and map them to DTOs
        List<LikeDTO> likes = likeService.getAllLikesByForumId(forumId).stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());

        // Return the response with a success message and the list of likes
        return ResponseEntity.ok(new ApiResponseDTO<>("Likes fetched successfully", likes));
    }

    /**
     * Toggle like on a forum.
     *
     * @param forumId     the forum ID
     * @param userDetails the authenticated user details
     * @return a response indicating whether the forum was liked or unliked
     */
    @PostMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<String>> toggleLikeOnForum(
            @PathVariable String forumId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Check if the user is authenticated
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>("User is not authenticated", null));
        }

        // Fetch the user by their email/username
        String username = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(username);

        // Check if the user exists
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO<>("User not found", null));
        }

        try {
            // Toggle the like status for the forum
            Map<String, String> response = likeService.toggleLikeOnForum(forumId, username);
            return ResponseEntity.ok(new ApiResponseDTO<>(response.get("message"), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null));
        }
    }

    /**
     * Fetch likes for a comment by its ID.
     *
     * @param commentId the comment ID
     * @return a response containing a list of likes for the specified comment
     */
        @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDTO<List<LikeDTO>>> getAllLikesByCommentId(@PathVariable String commentId) {
        List<LikeDTO> likes = likeService.getAllLikesByCommentId(commentId).stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseDTO<>("Likes fetched successfully", likes));
    }

    /**
     * Toggle like on a comment.
     *
     * @param commentId the comment ID
     * @param userDetails the authenticated user's details
     * @return a response indicating whether the comment was liked or unliked
     */
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDTO<String>> toggleLikeOnComment(
            @PathVariable String commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is authenticated
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>("User is not authenticated", null));
        }

        // Fetch the user by their email/username
        String username = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(username);

        // Check if the user exists
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO<>("User not found", null));
        }

        try {
            // Toggle the like status for the comment
            Map<String, String> response = likeService.toggleLikeOnComment(commentId, username);
            return ResponseEntity.ok(new ApiResponseDTO<>(response.get("message"), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/forums/{forumId}/check")
    public ResponseEntity<ApiResponseDTO<Map<String, Boolean>>> checkIfUserLikedForum(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String forumId) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>("User is not authenticated", null));
        }

        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO<>("User not found", null));
        }

        try {
            boolean isLiked = likeService.checkIfUserLikedForum(user.get().getUserId(), forumId);
            String message = isLiked ? "User has liked the forum" : "User has not liked the forum";

            Map<String, Boolean> responseData = new HashMap<>();
            responseData.put("isLiked", isLiked);

            return ResponseEntity.ok(new ApiResponseDTO<>(message, responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>("An error occurred: " + e.getMessage(), null));
        }
    }
}
