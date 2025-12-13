package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
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
    public ResponseEntity<ApiResponseDTO<ForumDTO>> getForumById(@PathVariable String forumId) {
        ForumDTO forumDTO = forumService.getForumById(forumId);
        ApiResponseDTO<ForumDTO> apiResponseDTO = new ApiResponseDTO<>("Forum fetched successfully", forumDTO);
        return ResponseEntity.ok(apiResponseDTO);
    }

    /**
     * Fetch top 3 featured forums with the most likes.
     *
     * @return a list of top featured forums
     */
    @GetMapping("/top-featured")
    public ResponseEntity<ApiResponseDTO<List<ForumDTO>>> getTopFeaturedForums() {
        List<ForumDTO> forumDTO = forumService.getTopFeaturedForums();
        ApiResponseDTO<List<ForumDTO>> apiResponseDTO =
                new ApiResponseDTO<>("Forum fetched successfully", forumDTO);
        return ResponseEntity.ok(apiResponseDTO);
    }

    /**
     * Search forums by keyword.
     *
     * @param keyword the search keyword
     * @return a list of matching forums
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ForumDTO>> searchForums(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {


        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ForumDTO> forums = forumService.searchForums(keyword, pageRequest);
        return ResponseEntity.ok(forums);
    }


    /**
     * Search forums by tags.
     *
     * @param tags the list of tags to search for
     * @return a response containing a list of matching forums
     */
    @GetMapping("/search-by-tags")
    public ResponseEntity<Page<ForumDTO>> searchForumsByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ForumDTO> forums = forumService.searchForumsByTags(tags, pageRequest);
        return ResponseEntity.ok(forums);
    }

    /**
     * Sort forums by a specified field.
     *
     * @param sortDir  the field to sort by
     * @return a list of sorted forums
     */
    @GetMapping("/sort")
    public ResponseEntity<List<ForumDTO>> sortForums(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        List<ForumDTO> forums = forumService.sortForums(sortBy, sortDir);
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
    public ResponseEntity<ApiResponseDTO<ForumDTO>> createForum(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ForumCreateDTO forumCreateDTO) {
        String username = userDetails.getUsername();
        ForumDTO createdForum = forumService.createForum(username, forumCreateDTO);
        ApiResponseDTO<ForumDTO> apiResponseDTO = new ApiResponseDTO<>("Forum created successfully", createdForum);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseDTO);
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
    public ResponseEntity<ApiResponseDTO<ForumDTO>> updateForum(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String forumId,@Valid @RequestBody UpdateForumDTO forumDTO) {
        String username = userDetails.getUsername();
        ForumDTO updatedForumDTO = forumService.updateForum(username,forumId , forumDTO);
        ApiResponseDTO<ForumDTO> apiResponseDTO = new ApiResponseDTO<>("Forum updated successfully", updatedForumDTO);
        return ResponseEntity.ok(apiResponseDTO);
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
        forumService.deleteForum(username,forumId);
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>("Forum deleted successfully", null);
        return ResponseEntity.ok(apiResponseDTO);
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = userDetails.getUsername();
        if (username == null) {
            ApiResponseDTO<Page<ForumDTO>> apiResponseDTO = new ApiResponseDTO<>("User is not authenticated", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        }
        Page<ForumDTO> forums = forumService.getMyForums(username, page, size);
        ApiResponseDTO<Page<ForumDTO>> apiResponseDTO = new ApiResponseDTO<>("My forums fetched successfully", forums);
        return ResponseEntity.ok(apiResponseDTO);
    }
}