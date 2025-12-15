package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.services.ForumService;
import com.petconnect.backend.utils.ResponseEntityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forums")
public class ForumController {
    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

    private final ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
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
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> getAllForums(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ForumDTO> forums = forumService.getAllForums(pageRequest);
        return ResponseEntityUtil.page(forums);
    }

    /**
     * Get a forum by its ID.
     *
     * @param forumId the forum ID
     * @return the forum details
     */
    @GetMapping("/{forumId}")
    public ResponseEntity<ApiResponseDTO<ForumDTO>> getForumById(@PathVariable String forumId) {
        ForumDTO forumDTO = forumService.getForumById(forumId);
        return ResponseEntityUtil.ok("Forum fetched successfully", forumDTO);
    }

    /**
     * Fetch top 3 featured forums with the most likes.
     *
     * @return a list of top featured forums
     */
    @GetMapping("/top-featured")
    public ResponseEntity<ApiResponseDTO<List<ForumDTO>>> getTopFeaturedForums() {
        List<ForumDTO> forumDTO = forumService.getTopFeaturedForums();
        return ResponseEntityUtil.ok("Top featured forums fetched successfully", forumDTO);
    }

    /**
     * Search forums by keyword.
     *
     * @param keyword the search keyword
     * @return a list of matching forums
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> searchForums(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ForumDTO> forums = forumService.searchForums(keyword, pageRequest);
        return ResponseEntityUtil.page(forums);
    }

    /**
     * Search forums by tags.
     *
     * @param tags the list of tags to search for
     * @return a response containing a list of matching forums
     */
    @GetMapping("/search-by-tags")
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> searchForumsByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ForumDTO> forums = forumService.searchForumsByTags(tags, pageRequest);
        return ResponseEntityUtil.page(forums);
    }

    /**
     * Sort forums by a specified field.
     *
     * @param sortDir  the field to sort by
     * @return a list of sorted forums
     */
    @GetMapping("/sort")
    public ResponseEntity<ApiResponseDTO<List<ForumDTO>>> sortForums(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        List<ForumDTO> forums = forumService.sortForums(sortBy, sortDir);
        return ResponseEntityUtil.ok("Forums sorted successfully", forums);
    }

    /**
     * Create a new forum.
     *
     * @param userDetails    the authenticated user details
     * @param forumCreateDTO the forum creation data
     * @return the created forum details
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ForumDTO>> createForum(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ForumCreateDTO forumCreateDTO) {
        String username = userDetails.getUsername();
        ForumDTO createdForum = forumService.createForum(username, forumCreateDTO);
        return ResponseEntityUtil.created("Forum created successfully", createdForum);
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
    public ResponseEntity<ApiResponseDTO<ForumDTO>> updateForum(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String forumId, @Valid @RequestBody UpdateForumDTO forumDTO) {
        String username = userDetails.getUsername();
        ForumDTO updatedForumDTO = forumService.updateForum(username, forumId, forumDTO);
        return ResponseEntityUtil.ok("Forum updated successfully", updatedForumDTO);
    }

    /**
     * Delete a forum by its ID.
     *
     * @param forumId     the forum ID
     * @param userDetails the authenticated user details
     * @return a response indicating the result of the deletion
     */
    @DeleteMapping("/{forumId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteForum(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String forumId) {
        String username = userDetails.getUsername();
        forumService.deleteForum(username, forumId);
        return ResponseEntityUtil.ok("Forum deleted successfully", null);
    }

    /**
     * Get forums created by the authenticated user.
     *
     * @param userDetails the authenticated user details
     * @return a list of forums created by the user
     */
    @GetMapping("/my-forums")
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> getMyForums(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        String username = userDetails.getUsername();
        if (username == null) {
            return ResponseEntityUtil.unauthorized("User is not authenticated");
        }
        Page<ForumDTO> forums = forumService.getMyForums(username, page, size);
        return ResponseEntityUtil.ok("My forums fetched successfully", forums);
    }
}