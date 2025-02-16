package com.petconnect.backend.entity;

import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@EntityListeners(AuditingEntityListener.class)
@Document(collection = "comments")
public class Comment {
    @Id
    private String commentId;

    @NotNull
    @Indexed
    private String forumId;

    @NotNull
    @Indexed
    private Long userId;

    @NotBlank
    private String text;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @DBRef
    private Forum forum;

    private AtomicInteger likes = new AtomicInteger(0);

    public Comment() {
    }

    public Comment(String commentId, String forumId, Long userId, String text, Date createdAt, Forum forum, AtomicInteger likes) {
        this.commentId = commentId;
        this.forumId = forumId;
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
        this.forum = forum;
        this.likes = likes;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public int getLikes() {
        return likes.get();
    }

    public void addLike() {
        this.likes.incrementAndGet();
    }

    public void removeLike() {
        this.likes.decrementAndGet();
    }
}
