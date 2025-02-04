    package com.petconnect.backend.controllers;

    import com.petconnect.backend.dto.ApiResponse;
    import com.petconnect.backend.dto.CommentDTO;
    import com.petconnect.backend.dto.ForumDTO;
    import com.petconnect.backend.dto.LikeDTO;
    import com.petconnect.backend.entity.Forum;
    import com.petconnect.backend.exceptions.ResourceNotFoundException;
    import com.petconnect.backend.services.ForumService;
    import com.petconnect.backend.mappers.ForumMapper;
    import jakarta.validation.Valid;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/forums")
    public class ForumController {
        private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

        private final ForumService forumService;

        @Autowired
        public ForumController(ForumService forumService) {
            this.forumService = forumService;
        }

        @GetMapping("/{forumId}")
        public ResponseEntity<ApiResponse<ForumDTO>> getForum(@PathVariable String forumId) {
            ForumDTO forumDTO = forumService.getForum(forumId);
            ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum fetched successfully", forumDTO);
            return ResponseEntity.ok(apiResponse);
        }

        @GetMapping
        public ResponseEntity<List<ForumDTO>> getAllForums() {
            List<ForumDTO> forums = forumService.getAllForums();
            return ResponseEntity.ok(forums);
        }


        @PostMapping
        public ResponseEntity<ApiResponse<ForumDTO>> createForum(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ForumDTO forumDTO) {
            ForumDTO createdForum = forumService.createForum(userDetails.getUsername(), forumDTO);
            ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum created successfully", createdForum);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }

        @PutMapping("/{forumId}")
        public ResponseEntity<ApiResponse<ForumDTO>> updateForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody ForumDTO forumDTO) {
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
        public ResponseEntity<ApiResponse<LikeDTO>> likeForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails) {
            LikeDTO like = forumService.likeForum(forumId, userDetails.getUsername());
            ApiResponse<LikeDTO> apiResponse = new ApiResponse<>("Forum liked successfully", like);
            return ResponseEntity.ok(apiResponse);
        }

        @PostMapping("/{forumId}/comment")
        public ResponseEntity<ApiResponse<CommentDTO>> commentOnForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CommentDTO commentDTO) {
            CommentDTO comment = forumService.commentOnForum(forumId, userDetails.getUsername(), commentDTO);
            ApiResponse<CommentDTO> apiResponse = new ApiResponse<>("Comment added successfully", comment);
            return ResponseEntity.ok(apiResponse);
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
