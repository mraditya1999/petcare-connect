package com.petconnect.backend.dto;

import java.util.Date;
import java.util.List;

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

    public ForumDTO() {
    }

    public ForumDTO(String forumId, String title, String content, List<String> tags, String firstName, String lastName, String email, Long likesCount, Long commentsCount, Date createdAt, Date updatedAt) {
        this.forumId = forumId;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
