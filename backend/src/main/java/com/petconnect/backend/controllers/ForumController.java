    package com.petconnect.backend.controllers;

    import com.petconnect.backend.dto.ApiResponse;
    import com.petconnect.backend.dto.ForumDTO;
    import com.petconnect.backend.entity.Forum;
    import com.petconnect.backend.services.ForumService;
    import com.petconnect.backend.mappers.ForumMapper;
    import jakarta.validation.Valid;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/forums")
    public class ForumController {
        private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

        private final ForumService forumService;
        private final ForumMapper forumMapper;

        @Autowired
        public ForumController(ForumService forumService, ForumMapper forumMapper) {
            this.forumService = forumService;
            this.forumMapper = forumMapper;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<ForumDTO>>> getAllForums() {
            logger.debug("Fetching all forums");
            List<ForumDTO> forums = forumService.getAllForums().stream()
                    .map(forumMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("Forums fetched successfully", forums));
        }

        @GetMapping("/{forumId}")
        public ResponseEntity<ApiResponse<ForumDTO>> getForumById(@PathVariable String forumId) {
            logger.debug("Fetching forum with ID: {}", forumId);
            Optional<Forum> forum = forumService.getForumById(forumId);
            return forum.map(value -> ResponseEntity.ok(new ApiResponse<>("Forum fetched successfully", forumMapper.toDTO(value)))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Forum not found. Please check the forum ID and try again.", null)));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<ForumDTO>> createForum(@Valid @RequestBody ForumDTO forumDTO) {
            logger.debug("Creating new forum");

            // Retrieve the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Get the username (or user ID) from the authentication object

            // Convert DTO to entity and set the user ID
            Forum forum = forumMapper.toEntity(forumDTO);
            forum.setUserId(username); // Set the user ID from the currently logged-in user

            Forum savedForum = forumService.createForum(forum);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Forum created successfully", forumMapper.toDTO(savedForum)));
        }

        @PutMapping("/{forumId}")
        public ResponseEntity<ApiResponse<ForumDTO>> updateForum(@PathVariable String forumId, @Valid @RequestBody ForumDTO forumDTO) {
            logger.debug("Updating forum with ID: {}", forumId);
            Optional<Forum> updatedForum = forumService.updateForum(forumId, forumMapper.toEntity(forumDTO));
            return updatedForum.map(value -> ResponseEntity.ok(new ApiResponse<>("Forum updated successfully", forumMapper.toDTO(value))))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>("Forum not found", null)));
        }

        @DeleteMapping("/{forumId}")
        public ResponseEntity<ApiResponse<Void>> deleteForum(@PathVariable String forumId) {
            logger.debug("Deleting forum with ID: {}", forumId);
            forumService.deleteForum(forumId);
            return ResponseEntity.ok(new ApiResponse<>("Forum deleted successfully", null));
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<ApiResponse<List<ForumDTO>>> getForumsByUserId(@PathVariable String userId) {
            logger.debug("Fetching forums by user ID: {}", userId);
            List<ForumDTO> forums = forumService.getForumsByUserId(userId).stream()
                    .map(forumMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("Forums fetched successfully", forums));
        }


    }
