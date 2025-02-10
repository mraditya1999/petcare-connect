package com.petconnect.backend.entity;

import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@Document(collection = "likes")

public class Like {
    @Id
    private String likeId;

    @NotNull
    @Indexed
    private String forumId;

    @NotNull
    @Indexed
    private String userId;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @DBRef
    private Forum forumPost;

    public Like() {
    }

    public Like(String likeId, String forumId, String userId, Date createdAt, Forum forumPost) {
        this.likeId = likeId;
        this.forumId = forumId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.forumPost = forumPost;
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

    public Forum getForumPost() {
        return forumPost;
    }

    public void setForumPost(Forum forumPost) {
        this.forumPost = forumPost;
    }
}