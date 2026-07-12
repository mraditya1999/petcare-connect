package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.forum.request.ForumCreateRequestDto;
import com.spring.petcareConnect.dtos.forum.request.ForumUpdateRequestDto;
import com.spring.petcareConnect.dtos.forum.response.ForumListResponseDto;
import com.spring.petcareConnect.dtos.forum.response.ForumResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ForumService {
    ForumListResponseDto getAllForumsOfUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ForumListResponseDto getAllForums(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ForumResponseDto createForumForUser(ForumCreateRequestDto forumCreateRequestDto);

    ForumResponseDto updateForumForUser(String forumId, ForumUpdateRequestDto forumUpdateRequestDto);

    ForumResponseDto getForumById(String forumId);

    void deleteForumForUser(String forumId);

    ForumListResponseDto searchForums(String keyword, List<String> tags, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ForumListResponseDto getTopFeaturedForums(Integer pageNumber, Integer pageSize);
}
