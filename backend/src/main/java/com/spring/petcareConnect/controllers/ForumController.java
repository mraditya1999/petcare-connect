package com.spring.petcareConnect.controllers;


import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.forum.request.ForumCreateRequestDto;
import com.spring.petcareConnect.dtos.forum.request.ForumUpdateRequestDto;
import com.spring.petcareConnect.dtos.forum.response.ForumListResponseDto;
import com.spring.petcareConnect.dtos.forum.response.ForumResponseDto;
import com.spring.petcareConnect.dtos.pet.response.PetListResponseDto;
import com.spring.petcareConnect.services.ForumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        CustomApiResponse<ForumListResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.FORUM_FETCHED, forumListResponseDto);
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

}
