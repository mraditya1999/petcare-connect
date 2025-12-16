package com.petconnect.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumDTO {
    private String forumId;
    private String title;
    private String content;
    private List<String> tags;
    private String firstName;
    private String lastName;
    private String email;
    private Long likesCount = 0L;
    private Long commentsCount = 0L;
    private Date createdAt;
    private Date updatedAt;
    private Long userId;
}
