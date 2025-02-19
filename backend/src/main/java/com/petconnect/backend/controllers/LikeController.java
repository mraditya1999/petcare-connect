package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.LikeDTO;
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

//    @GetMapping
//    public ResponseEntity<ApiResponse<List<LikeDTO>>> getAllLikes() {
//        logger.debug("Fetching all likes");
//        List<LikeDTO> likes = likeService.getAllLikes().stream()
//                .map(likeMapper::toDTO)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(new ApiResponse<>("Likes fetched successfully", likes));
//    }
//
//    @GetMapping("/{likeId}")
//    public ResponseEntity<ApiResponse<LikeDTO>> getLikeById(@PathVariable String likeId) {
//        logger.debug("Fetching like with ID: {}", likeId);
//        Optional<Like> like = likeService.getLikeById(likeId);
//        return like.map(value -> ResponseEntity.ok(new ApiResponse<>("Like fetched successfully", likeMapper.toDTO(value))))
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ApiResponse<>("Like not found", null)));
//    }

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
        if (!user.isPresent()) {
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
        if (!user.isPresent()) {
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

//    @PostMapping
//    public ResponseEntity<ApiResponse<LikeDTO>> createLike(@Valid @RequestBody LikeDTO likeDTO) {
//        logger.debug("Creating new like");
//        Like like = likeMapper.toEntity(likeDTO);
//        Like savedLike = likeService.createLike(like);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new ApiResponse<>("Like created successfully", likeMapper.toDTO(savedLike)));
//    }
//
//    @DeleteMapping("/{likeId}")
//    public ResponseEntity<ApiResponse<Void>> deleteLike(@PathVariable String likeId) {
//        logger.debug("Deleting like with ID: {}", likeId);
//        likeService.deleteLike(likeId);
//        return ResponseEntity.ok(new ApiResponse<>("Like deleted successfully", null));
//    }
//
//    @PostMapping("/forum/{forumId}/like")
//    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(
//            @PathVariable String forumId,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        if (userDetails == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("User is not authenticated", null));
//        }
//
//        String username = userDetails.getUsername();
//        Optional<User> user = userRepository.findByEmail(username);
//
//        if (!user.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("User not found", null));
//        }
//
//        Long userId = user.get().getUserId();
//        long likesCount;
//
//        try {
//            likeService.toggleLike(userId, forumId);
//            likesCount = likeService.getLikesCountForForum(forumId);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), null));
//        }
//
//        boolean liked = likeService.checkIfUserLikedForum(userId, forumId);
//        Map<String, Object> response = new HashMap<>();
//        response.put("liked", liked);
//        response.put("likesCount", likesCount);
//        return ResponseEntity.ok(new ApiResponse<>("Like toggled", response));
//    }
}
