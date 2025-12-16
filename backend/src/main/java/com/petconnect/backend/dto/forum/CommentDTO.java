package com.petconnect.backend.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String commentId;
    private String forumId;
    private Long userId;
    private String text;
    private Date createdAt;
    private String firstName;
    private String lastName;
    private String email;
    private String parentId;
    private Set<Long> likedByUsers = new HashSet<>();
    private Set<CommentDTO> replies = new HashSet<>();
}
