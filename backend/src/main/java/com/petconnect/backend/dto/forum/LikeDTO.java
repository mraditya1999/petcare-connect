package com.petconnect.backend.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {
    private String likeId;
    private String forumId;
    private String userId;
    private Date createdAt;
}

