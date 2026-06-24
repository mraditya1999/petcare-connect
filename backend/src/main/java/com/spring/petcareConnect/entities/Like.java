package com.spring.petcareConnect.entities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@CompoundIndexes({
        @CompoundIndex(name = "idx_forum_user", def = "{'forum_id': 1, 'user_id': 1}", unique = true),
        @CompoundIndex(name = "idx_comment_user", def = "{'comment_id': 1, 'user_id': 1}", unique = true),
        @CompoundIndex(name = "idx_user_created", def = "{'user_id': 1, 'created_at': -1}")
})
public class Like {

    @Id
    @EqualsAndHashCode.Include
    @Field("like_id")
    private String likeId;

    /**
     * Forum ID that was liked (nullable, either this or commentId)
     */
    @Field("forum_id")
    @Indexed
    private String forumId;

    /**
     * Comment ID that was liked (nullable, either this or forumId)
     */
    @Field("comment_id")
    @Indexed
    private String commentId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Field("user_id")
    @Indexed
    private Long userId;

    @Field("is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    /**
     * Check if this is a forum like
     */
    public boolean isForumLike() {
        return forumId != null && !forumId.trim().isEmpty();
    }

    /**
     * Check if this is a comment like
     */
    public boolean isCommentLike() {
        return commentId != null && !commentId.trim().isEmpty();
    }

    /**
     * Validation - ensure either forum or comment is set, not both
     */
    public void validateLike() {
        boolean hasForumId = forumId != null && !forumId.trim().isEmpty();
        boolean hasCommentId = commentId != null && !commentId.trim().isEmpty();

        if (!hasForumId && !hasCommentId) {
            throw new IllegalArgumentException("Either forumId or commentId must be provided");
        }
        if (hasForumId && hasCommentId) {
            throw new IllegalArgumentException("Only one of forumId or commentId should be set");
        }
    }
}