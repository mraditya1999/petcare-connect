package com.petconnect.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ElementCollection
    private Set<Long> likedByUsers = new HashSet<>();

    @Field("parent_id") // Add parent_id field for DB
    private String parentId;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "commentId", insertable = false, updatable = false) // Join using parent_id
    private Comment parentComment;

    @DBRef // Use DBRef for replies
    @OneToMany(mappedBy = "parentComment") // Remove cascade here
    private Set<Comment> replies = new HashSet<>();

    public Comment() {
    }

    public Comment(String commentId, String forumId, Long userId, String text, Date createdAt, Set<Long> likedByUsers, String parentId, Comment parentComment, Set<Comment> replies) {
        this.commentId = commentId;
        this.forumId = forumId;
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
        this.likedByUsers = likedByUsers;
        this.parentId = parentId;
        this.parentComment = parentComment;
        this.replies = replies;
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

    public Set<Long> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<Long> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public Set<Comment> getReplies() {
        return replies;
    }

    public void setReplies(Set<Comment> replies) {
        this.replies = replies;
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(commentId, comment.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", forumId='" + forumId + '\'' +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", likedByUsers=" + likedByUsers +
                ", parentId='" + parentId + '\'' +
                ", parentComment=" + parentComment +
                ", replies=" + replies +
                '}';
    }
}
