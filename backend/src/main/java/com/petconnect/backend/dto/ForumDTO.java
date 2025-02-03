package com.petconnect.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ForumDTO {
    private String forumId;
    private String userId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private List<CommentDTO> comments;
    private List<LikeDTO> likes;

    public ForumDTO() {
    }

    public ForumDTO(String forumId, String userId, String title, String content ,List<CommentDTO> comments, List<LikeDTO> likes) {
        this.forumId = forumId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.comments = comments;
        this.likes = likes;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<LikeDTO> getLikes() {
        return likes;
    }

    public void setLikes(List<LikeDTO> likes) {
        this.likes = likes;
    }
}
