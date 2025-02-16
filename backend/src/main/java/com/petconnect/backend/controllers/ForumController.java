package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.services.CommentService;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/forums")
public class ForumController {
    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

    private final ForumService forumService;
    private final CommentService commentService;

    @Autowired
    public ForumController(ForumService forumService, CommentService commentService) {
        this.forumService = forumService;
        this.commentService = commentService;
    }

    /**
     * Get all forums with pagination and sorting.
     *
     * @param page    the page number
     * @param size    the page size
     * @param sortBy  the field to sort by
     * @param sortDir the sort direction (asc/desc)
     * @return a page of forums
     */
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

    /**
     * Get a forum by its ID.
     *
     * @param forumId the forum ID
     * @return the forum details
     */
    @GetMapping("/{forumId}")
    public ResponseEntity<ApiResponse<ForumDTO>> getForumById(@PathVariable String forumId) {
        ForumDTO forumDTO = forumService.getForumById(forumId);
        ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum fetched successfully", forumDTO);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Fetch top 3 featured forums with the most likes.
     *
     * @return a list of top featured forums
     */
    @GetMapping("/top-featured")
    public ResponseEntity<List<ForumDTO>> getTopFeaturedForums() {
        List<ForumDTO> forums = forumService.getTopFeaturedForums();
        return ResponseEntity.ok(forums);
    }

    /**
     * Search forums by keyword.
     *
     * @param keyword the search keyword
     * @return a list of matching forums
     */
    @GetMapping("/search")
    public ResponseEntity<List<ForumDTO>> searchForums(@RequestParam String keyword) {
        List<ForumDTO> forums = forumService.searchForums(keyword);
        return ResponseEntity.ok(forums);
    }

    /**
     * Search forums by tags.
     *
     * @param tags the list of tags to search for
     * @return a response containing a list of matching forums
     */
    @GetMapping("/search-by-tags")
    public ResponseEntity<ApiResponse<List<ForumDTO>>> searchForumsByTags(@RequestParam List<String> tags) {
        List<ForumDTO> forums = forumService.searchForumsByTags(tags);
        ApiResponse<List<ForumDTO>> apiResponse = new ApiResponse<>("Forums fetched successfully", forums);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Sort forums by a specified field.
     *
     * @param field the field to sort by
     * @return a list of sorted forums
     */
    @GetMapping("/sort")
    public ResponseEntity<List<ForumDTO>> sortForums(@RequestParam String field) {
        List<ForumDTO> forums = forumService.sortForums(field);
        return ResponseEntity.ok(forums);
    }


    /**
     * Create a new forum.
     *
     * @param userDetails    the authenticated user details
     * @param forumCreateDTO the forum creation data
     * @return the created forum details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ForumDTO>> createForum(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ForumCreateDTO forumCreateDTO) {
        String username = userDetails.getUsername();
        ForumDTO createdForum = forumService.createForum(username, forumCreateDTO);
        ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum created successfully", createdForum);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Update an existing forum.
     *
     * @param forumId     the forum ID
     * @param userDetails the authenticated user details
     * @param forumDTO    the forum update data
     * @return the updated forum details
     */
    @PutMapping("/{forumId}")
    public ResponseEntity<ApiResponse<ForumDTO>> updateForum(@AuthenticationPrincipal UserDetails userDetails,@PathVariable String forumId, @RequestBody UpdateForumDTO forumDTO) {
        try {
            String username = userDetails.getUsername();
            ForumDTO updatedForumDTO = forumService.updateForum(username,forumId , forumDTO);
            ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum updated successfully", updatedForumDTO);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            logger.error("Error updating forum: {}", e.getMessage());
            ApiResponse<ForumDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating forum: {}", e.getMessage());
            ApiResponse<ForumDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error updating forum", e);
            ApiResponse<ForumDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Delete a forum by its ID.
     *
     * @param forumId     the forum ID
     * @param userDetails the authenticated user details
     * @return a response indicating the result of the deletion
     */
    @DeleteMapping("/{forumId}")
    public ResponseEntity<ApiResponse<Void>> deleteForum(@AuthenticationPrincipal UserDetails userDetails,@PathVariable String forumId) {
        try {
            String username = userDetails.getUsername();
            forumService.deleteForum(username,forumId);
            ApiResponse<Void> apiResponse = new ApiResponse<>("Forum deleted successfully", null);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            logger.error("Error deleting forum: {}", e.getMessage());
            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting forum: {}", e.getMessage());
            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error deleting forum", e);
            ApiResponse<Void> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get forums created by the authenticated user.
     *
     * @param userDetails the authenticated user details
     * @return a list of forums created by the user
     */
    @GetMapping("/my-forums")
    public ResponseEntity<ApiResponse<Page<ForumDTO>>> getMyForums(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = userDetails.getUsername();
        if (username == null) {
            ApiResponse<Page<ForumDTO>> apiResponse = new ApiResponse<>("User is not authenticated", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
        Page<ForumDTO> forums = forumService.getMyForums(username, page, size);
        ApiResponse<Page<ForumDTO>> apiResponse = new ApiResponse<>("My forums fetched successfully", forums);
        return ResponseEntity.ok(apiResponse);
    }


//    /**
//     * Toggle like on a forum.
//     *
//     * @param forumId     the forum ID
//     * @param userDetails the authenticated user details
//     * @return a response indicating whether the forum was liked or unliked
//     */
//    @PostMapping("/{forumId}/like")
//    public ResponseEntity<ApiResponse<String>> toggleLikeOnForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails) {
//        Map<String, String> result = forumService.toggleLikeOnForum(forumId, userDetails.getUsername());
//        ApiResponse<String> apiResponse = new ApiResponse<>(result.get("message"));
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    /**
//     * Add a comment to a forum.
//     *
//     * @param forumId     the forum ID
//     * @param userDetails the authenticated user details
//     * @param commentDTO  the comment data
//     * @return the added comment details
//     */
//    @PostMapping("/{forumId}/comment")
//    public ResponseEntity<ApiResponse<CommentDTO>> commentOnForum(@PathVariable String forumId, @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CommentDTO commentDTO) {
//        CommentDTO comment = forumService.commentOnForum(forumId, userDetails.getUsername(), commentDTO);
//        ApiResponse<CommentDTO> apiResponse = new ApiResponse<>("Comment added successfully", comment);
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    /**
//     * Delete a comment from a forum.
//     *
//     * @param forumId     the forum ID
//     * @param commentId   the comment ID
//     * @param userDetails the authenticated user details
//     * @return a response indicating the result of the deletion
//     */
//    @DeleteMapping("/{forumId}/comment/{commentId}")
//    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String forumId, @PathVariable String commentId, @AuthenticationPrincipal UserDetails userDetails) {
//        try {
//            forumService.deleteComment(forumId, commentId, userDetails.getUsername());
//            ApiResponse<Void> apiResponse = new ApiResponse<>("Comment deleted successfully", null);
//            return ResponseEntity.ok(apiResponse);
//        } catch (ResourceNotFoundException e) {
//            logger.error("Error deleting comment: {}", e.getMessage());
//            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (IllegalArgumentException e) {
//            logger.error("Error deleting comment: {}", e.getMessage());
//            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        } catch (Exception e) {
//            logger.error("Unexpected error deleting comment", e);
//            ApiResponse<Void> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//        @PostMapping("/{forumId}/reply/{parentId}")
//        public ResponseEntity<ApiResponse<CommentDTO>> replyToComment(
//                @PathVariable String forumId,
//                @PathVariable String parentId,
//                @AuthenticationPrincipal UserDetails userDetails,
//                @Valid @RequestBody CommentDTO commentDTO) {
//            CommentDTO reply = commentService.replyToComment(forumId, userDetails.getUsername(), commentDTO, parentId);
//            ApiResponse<CommentDTO> apiResponse = new ApiResponse<>("Reply added successfully", reply);
//            return ResponseEntity.ok(apiResponse);
//        }

}