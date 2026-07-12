package com.spring.petcareConnect.controllers;


import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.forum.request.ForumCreateRequestDto;
import com.spring.petcareConnect.dtos.forum.request.ForumUpdateRequestDto;
import com.spring.petcareConnect.dtos.forum.response.ForumListResponseDto;
import com.spring.petcareConnect.dtos.forum.response.ForumResponseDto;
import com.spring.petcareConnect.services.ForumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forums")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("my-forums")
    public ResponseEntity<CustomApiResponse<ForumListResponseDto>> getAllForumsOfUser(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_FORUM_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {

        ForumListResponseDto forumListResponseDto = forumService.getAllForumsOfUser(pageNumber, pageSize, sortBy, sortOrder);
        CustomApiResponse<ForumListResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.FORUMS_FETCHED, forumListResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<ForumListResponseDto>> getAllForums(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_FORUM_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {

        ForumListResponseDto forumListResponseDto = forumService.getAllForums(pageNumber, pageSize, sortBy, sortOrder);
        CustomApiResponse<ForumListResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.FORUMS_FETCHED, forumListResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{forumId}")
    public ResponseEntity<CustomApiResponse<ForumResponseDto>> getForumById(@PathVariable String forumId) {
        ForumResponseDto forumResponseDto = forumService.getForumById(forumId);
        CustomApiResponse<ForumResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.FORUM_FETCHED, forumResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<ForumResponseDto>> createForumForUser(@RequestBody @Valid ForumCreateRequestDto forumCreateRequestDto) {
        ForumResponseDto forumResponseDto = forumService.createForumForUser(forumCreateRequestDto);
        CustomApiResponse<ForumResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.FORUM_CREATED, forumResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{forumId}")
    public ResponseEntity<CustomApiResponse<ForumResponseDto>> updateForumForUser(@PathVariable String forumId, @RequestBody @Valid ForumUpdateRequestDto forumUpdateRequestDto) {
        ForumResponseDto updatedForum = forumService.updateForumForUser(forumId, forumUpdateRequestDto);
        CustomApiResponse<ForumResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.FORUM_UPDATED, updatedForum);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{forumId}")
    public ResponseEntity<CustomApiResponse<String>> deleteForum(@PathVariable String forumId) {
        forumService.deleteForumForUser(forumId);
        CustomApiResponse<String> response = new CustomApiResponse<>(true, ResponseMessages.FORUM_DELETED, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<CustomApiResponse<ForumListResponseDto>> searchForums(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "tags", required = false) List<String> tags,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_FORUM_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER) String sortOrder
    ) {
        ForumListResponseDto forums = forumService.searchForums(keyword, tags, pageNumber, pageSize, sortBy, sortOrder);
        CustomApiResponse<ForumListResponseDto> response =
                new CustomApiResponse<>(true, ResponseMessages.FORUMS_FETCHED, forums);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/top-featured")
    public ResponseEntity<CustomApiResponse<ForumListResponseDto>> getTopFeaturedForums(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize
    ) {
        ForumListResponseDto forums = forumService.getTopFeaturedForums(pageNumber, pageSize);
        CustomApiResponse<ForumListResponseDto> response =
                new CustomApiResponse<>(true, ResponseMessages.FEATURED_FORUMS_FETCHED, forums);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
