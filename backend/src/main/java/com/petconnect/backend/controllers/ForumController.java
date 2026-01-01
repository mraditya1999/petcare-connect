package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.forum.ForumCreateDTO;
import com.petconnect.backend.dto.forum.ForumDTO;
import com.petconnect.backend.dto.forum.UpdateForumDTO;
import com.petconnect.backend.services.ForumService;
import com.petconnect.backend.utils.PaginationUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forums")
@Tag(name = "Forums", description = "APIs for forum creation, search, and management")
public class ForumController extends BaseController {

    private final ForumService forumService;

    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

    public ForumController(ForumService forumService) {
        super(logger);
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
    @Operation(
            summary = "Get all forums",
            description = "Fetch all forums with pagination and sorting"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forums fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> getAllForums(
            @RequestParam(defaultValue = "0") @Min(0)
            @Parameter(description = "Page number (0-based)", example = "0")
            int page,

            @RequestParam(defaultValue = "10") @Min(1)
            @Parameter(description = "Page size", example = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            @Parameter(description = "Sort field", example = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            @Parameter(description = "Sort direction (asc or desc)", example = "desc")
            String sortDir
    ) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, Sort.Direction.fromString(sortDir));
        Page<ForumDTO> forums = forumService.getAllForums(pageable);
        return ResponseEntityUtil.page(forums);
    }

    /**
     * Get a forum by its ID.
     *
     * @param forumId the forum ID
     * @return the forum details
     */
    @Operation(
            summary = "Get forum by ID",
            description = "Fetch forum details using forum ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forum fetched successfully",
                    content = @Content(schema = @Schema(implementation = ForumDTO.class))),
            @ApiResponse(responseCode = "404", description = "Forum not found")
    })
    @GetMapping("/{forumId}")
    public ResponseEntity<ApiResponseDTO<ForumDTO>> getForumById(
            @Parameter(description = "Forum ID", required = true)
            @PathVariable String forumId) {
        ForumDTO forumDTO = forumService.getForumById(forumId);
        return ResponseEntityUtil.ok("Forum fetched successfully", forumDTO);
    }

    /**
     * Fetch top 3 featured forums with the most likes.
     *
     * @return a list of top featured forums
     */
    @Operation(
            summary = "Get top featured forums",
            description = "Fetch top 3 featured forums with highest likes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top featured forums fetched successfully")
    })
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
    @Operation(
            summary = "Search forums by keyword",
            description = "Search forums using a keyword with pagination and sorting"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results fetched successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> searchForums(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam String keyword,

            @RequestParam(defaultValue = "0") @Min(0)
            int page,

            @RequestParam(defaultValue = "10") @Min(1)
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDir
    ) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, Sort.Direction.fromString(sortDir));

        Page<ForumDTO> forums = forumService.searchForums(keyword, pageable);
        return ResponseEntityUtil.page(forums);
    }

    /**
     * Search forums by tags.
     *
     * @param tags the list of tags to search for
     * @return a response containing a list of matching forums
     */
    @Operation(
            summary = "Search forums by tags",
            description = "Search forums using one or more tags"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forums fetched successfully")
    })
    @GetMapping("/search-by-tags")
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> searchForumsByTags(
            @Parameter(description = "List of tags", example = "[\"pets\",\"health\"]")
            @RequestParam List<String> tags,

            @RequestParam(defaultValue = "0") @Min(0)
            int page,

            @RequestParam(defaultValue = "10") @Min(1)
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDir
    ) {

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, Sort.Direction.fromString(sortDir));

        Page<ForumDTO> forums = forumService.searchForumsByTags(tags, pageable);
        return ResponseEntityUtil.page(forums);
    }

    /**
     * Sort forums by a specified field.
     *
     * @param sortDir  the field to sort by
     * @return a list of sorted forums
     */
    @Operation(
            summary = "Sort forums",
            description = "Fetch forums sorted by a given field"
    )
    @GetMapping("/sort")
    public ResponseEntity<ApiResponseDTO<List<ForumDTO>>> sortForums(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
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
    @Operation(
            summary = "Create a forum",
            description = "Create a new forum (authentication required)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Forum created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ForumDTO>> createForum(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Valid @RequestBody ForumCreateDTO forumCreateDTO) {
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
    @Operation(
            summary = "Update forum",
            description = "Update an existing forum (only owner can update)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{forumId}")
    public ResponseEntity<ApiResponseDTO<ForumDTO>> updateForum(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @PathVariable String forumId,
            @Valid @RequestBody UpdateForumDTO forumDTO) {
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
    @Operation(
            summary = "Delete forum",
            description = "Delete a forum by ID (only owner can delete)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{forumId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteForum(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @PathVariable String forumId) {
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
    @Operation(
            summary = "Get my forums",
            description = "Fetch forums created by the authenticated user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my-forums")
    public ResponseEntity<ApiResponseDTO<Page<ForumDTO>>> getMyForums(
            @Parameter(hidden = true)
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