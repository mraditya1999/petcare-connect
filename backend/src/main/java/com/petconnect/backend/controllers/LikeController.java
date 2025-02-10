package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.LikeDTO;
import com.petconnect.backend.entity.Like;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.LikeService;
import com.petconnect.backend.mappers.LikeMapper;
import jakarta.validation.Valid;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<LikeDTO>>> getAllLikes() {
        logger.debug("Fetching all likes");
        List<LikeDTO> likes = likeService.getAllLikes().stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Likes fetched successfully", likes));
    }

    @GetMapping("/{likeId}")
    public ResponseEntity<ApiResponse<LikeDTO>> getLikeById(@PathVariable String likeId) {
        logger.debug("Fetching like with ID: {}", likeId);
        Optional<Like> like = likeService.getLikeById(likeId);
        return like.map(value -> ResponseEntity.ok(new ApiResponse<>("Like fetched successfully", likeMapper.toDTO(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Like not found", null)));
    }

    @GetMapping("/forum/{forumId}")
    public ResponseEntity<ApiResponse<List<LikeDTO>>> getLikesByForumId(@PathVariable String forumId) {
        logger.debug("Fetching likes for forum ID: {}", forumId);
        List<LikeDTO> likes = likeService.getLikesByForumId(forumId).stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Likes fetched successfully", likes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LikeDTO>> createLike(@Valid @RequestBody LikeDTO likeDTO) {
        logger.debug("Creating new like");
        Like like = likeMapper.toEntity(likeDTO);
        Like savedLike = likeService.createLike(like);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Like created successfully", likeMapper.toDTO(savedLike)));
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<ApiResponse<Void>> deleteLike(@PathVariable String likeId) {
        logger.debug("Deleting like with ID: {}", likeId);
        likeService.deleteLike(likeId);
        return ResponseEntity.ok(new ApiResponse<>("Like deleted successfully", null));
    }

    @GetMapping("/forum/{forumId}/checklike")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkLikeStatus(
            @PathVariable String forumId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.ok(new ApiResponse<>("User is not authenticated", Map.of("liked", false, "likesCount", 0)));
        }

        String username = userDetails.getUsername(); // Get username
        Optional<User> user = userRepository.findByEmail(username); // Find user by username

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("User not found", null));
        }

        Long userId = user.get().getUserId(); // Now you have the userId
        boolean liked = likeService.checkIfUserLikedForum(String.valueOf(userId), forumId);
        long likesCount = likeService.getLikesCountForForum(forumId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likesCount", likesCount);

        return ResponseEntity.ok(new ApiResponse<>("Like status checked", response));
    }

    @PostMapping("/forum/{forumId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(
            @PathVariable String forumId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("User is not authenticated", null));
        }

        String username = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(username);

        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("User not found", null));
        }

        Long userId = user.get().getUserId();
        long likesCount;

        try {
            likeService.toggleLike(String.valueOf(userId), forumId);
            likesCount = likeService.getLikesCountForForum(forumId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), null));
        }

        boolean liked = likeService.checkIfUserLikedForum(String.valueOf(userId), forumId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likesCount", likesCount);
        return ResponseEntity.ok(new ApiResponse<>("Like toggled", response));
    }
}
