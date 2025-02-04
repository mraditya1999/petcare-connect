package com.petconnect.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class LikeDTO {
    private String likeId;
    @NotNull
    private String forumId;
    @NotNull
    private String userId;
    private Date createdAt;

    public LikeDTO() {
    }

    public LikeDTO(String likeId, String forumId, String userId, Date createdAt) {
        this.likeId = likeId;
        this.forumId = forumId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}