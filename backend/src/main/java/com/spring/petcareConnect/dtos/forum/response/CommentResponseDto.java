package com.spring.petcareConnect.dtos.forum.response;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private String commentId;
    private String forumId;
    private Long userId;
    private String text;
    private String parentId;

    private Integer childCount;
    private Integer likeCount;

    private Boolean isEdited;

    private Instant createdAt;
    private Instant updatedAt;
}
