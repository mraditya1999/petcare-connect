    package com.petconnect.backend.controllers;

    import com.petconnect.backend.dto.*;
    import com.petconnect.backend.exceptions.ResourceNotFoundException;
    import com.petconnect.backend.services.ForumService;
    import jakarta.validation.Valid;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.bind.annotation.*;
    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/forums")
    public class ForumController {
        private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

        private final ForumService forumService;

        @Autowired
        public ForumController(ForumService forumService) {
            this.forumService = forumService;
        }

        @GetMapping
        public ResponseEntity<Page<ForumDTO>> getAllForums(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "createdAt") String sortBy,
                @RequestParam(defaultValue = "asc") String sortDir) {

            Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<ForumDTO> forums = forumService.getAllForums(pageRequest);

            return ResponseEntity.ok(forums);
        }


        @GetMapping("/{forumId}")
        public ResponseEntity<ApiResponse<ForumDTO>> getForumById(@PathVariable String forumId) {
            ForumDTO forumDTO = forumService.getForumById(forumId);
            ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum fetched successfully", forumDTO);
            return ResponseEntity.ok(apiResponse);
        }

        @PostMapping
        public ResponseEntity<ApiResponse<ForumDTO>> createForum(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ForumCreateDTO forumCreateDTO) {
            ForumDTO createdForum = forumService.createForum(userDetails.getUsername(), forumCreateDTO);
            ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum created successfully", createdForum);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }

        @PutMapping("/{forumId}")
        public ResponseEntity<ApiResponse<ForumDTO>> updateForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateForumDTO forumDTO) {
            try {
                ForumDTO updatedForumDTO = forumService.updateForum(forumId, userDetails.getUsername(), forumDTO);
                ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum updated successfully", updatedForumDTO);
                return ResponseEntity.ok(apiResponse);
            } catch (ResourceNotFoundException e) {
                ApiResponse<ForumDTO> response = new ApiResponse<>(e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } catch (IllegalArgumentException e) {
                ApiResponse<ForumDTO> response = new ApiResponse<>(e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } catch (Exception e) {
                e.printStackTrace();
                ApiResponse<ForumDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }

        // Search forums by keyword
        @GetMapping("/search")
        public ResponseEntity<List<ForumDTO>> searchForums(@RequestParam String keyword) {
            List<ForumDTO> forums = forumService.searchForums(keyword);
            return ResponseEntity.ok(forums);
        }

        // Sort forums by a specified field
        @GetMapping("/sort")
        public ResponseEntity<List<ForumDTO>> sortForums(@RequestParam String field) {
            List<ForumDTO> forums = forumService.sortForums(field);
            return ResponseEntity.ok(forums);
        }

        // Fetch top 3 featured forums with the most likes
        @GetMapping("/top-featured")
        public ResponseEntity<List<ForumDTO>> getTopFeaturedForums() {
            List<ForumDTO> forums = forumService.getTopFeaturedForums();
            return ResponseEntity.ok(forums);
        }

        @DeleteMapping("/{forumId}")
        public ResponseEntity<ApiResponse<Void>> deleteForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails) {
            try {
                forumService.deleteForum(forumId, userDetails);
                ApiResponse<Void> apiResponse = new ApiResponse<>("Forum deleted successfully", null);
                return ResponseEntity.ok(apiResponse);
            } catch (ResourceNotFoundException e) {
                ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } catch (IllegalArgumentException e) {
                ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } catch (Exception e) {
                e.printStackTrace();
                ApiResponse<Void> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }

        @PostMapping("/{forumId}/like")
        public ResponseEntity<ApiResponse<String>> toggleLikeOnForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails) {
            Map<String, String> result = forumService.toggleLikeOnForum(forumId, userDetails.getUsername());
            ApiResponse<String> apiResponse = new ApiResponse<>(result.get("message"));
            return ResponseEntity.ok(apiResponse);
        }

        @PostMapping("/{forumId}/comment")
        public ResponseEntity<ApiResponse<CommentDTO>> commentOnForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CommentDTO commentDTO) {
            CommentDTO comment = forumService.commentOnForum(forumId, userDetails.getUsername(), commentDTO);
            ApiResponse<CommentDTO> apiResponse = new ApiResponse<>("Comment added successfully", comment);
            return ResponseEntity.ok(apiResponse);
        }

        @DeleteMapping("/{forumId}/comment/{commentId}")
        public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String forumId, @PathVariable String commentId, @AuthenticationPrincipal UserDetails userDetails) {
            try {
                forumService.deleteComment(forumId, commentId, userDetails.getUsername());
                ApiResponse<Void> apiResponse = new ApiResponse<>("Comment deleted successfully", null);
                return ResponseEntity.ok(apiResponse);
            } catch (ResourceNotFoundException e) {
                ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } catch (IllegalArgumentException e) {
                ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } catch (Exception e) {
                e.printStackTrace();
                ApiResponse<Void> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }


        @GetMapping("/my-forums")
        public ResponseEntity<List<ForumDTO>> getMyForums(@AuthenticationPrincipal UserDetails userDetails) {
            if (userDetails == null) {
                ApiResponse<List<ForumDTO>> apiResponse = new ApiResponse<>("User is not authenticated", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse.getData());
            }
            List<ForumDTO> forums = forumService.getMyForums(userDetails.getUsername());
            return ResponseEntity.ok(forums);
        }

    }
