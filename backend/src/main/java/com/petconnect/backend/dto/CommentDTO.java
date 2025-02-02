package com.petconnect.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class CommentDTO {
    private String commentId;
    @NotNull
    private String forumId;
    @NotNull
    private String userId;
    @NotBlank
    private String text;
    private Date createdAt;

    public CommentDTO() {
    }

    public CommentDTO(String commentId, String forumId, String userId, String text, Date createdAt) {
        this.commentId = commentId;
        this.forumId = forumId;
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}