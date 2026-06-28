package com.spring.petcareConnect.dtos.forum.response;

import com.spring.petcareConnect.enums.ForumTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumResponseDto {
    private String forumId;
    private Long userId;
    private String title;
    private String content;
    private Set<String> tags;
    private Boolean published;
    private Boolean isDeleted;
    private String deletionReason;
    private Integer commentCount;
    private Integer likeCount;
    private Boolean isPinned;
    private Boolean isClosed;
    private ForumTag category;
    private Long viewCount;
    private Boolean isLocked;
    private Boolean likedByCurrentUser;
    private Boolean visible;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isFeatured;

}

