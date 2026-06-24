package com.spring.petcareConnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"likedByUsers"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {

    @Id
    @EqualsAndHashCode.Include
    @Field("comment_id")
    @Indexed
    private String commentId;

    @NotBlank(message = "Forum ID is required")
    @Indexed
    @Field("forum_id")
    private String forumId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Indexed
    @Field("user_id")
    private Long userId;

    @NotBlank(message = "Comment text is required")
    @Size(min = 2, max = 2000, message = "Comment must be between 2 and 2000 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Comment cannot be only whitespace")
    private String text;

    /**
     * Parent comment ID for nested/threaded comments (replies)
     * Null if this is a top-level comment
     */
    @Field("parent_id")
    private String parentId;

    /**
     * Count of direct child comments (cached for performance)
     */
    @Field("child_count")
    private Integer childCount = 0;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("is_deleted")
    private Boolean isDeleted = false;

    @Field("liked_by_users")
    @JsonIgnore
    private Set<Long> likedByUsers = new HashSet<>();

    @Field("like_count")
    private Integer likeCount = 0;

    @Field("is_edited")
    private Boolean isEdited = false;

    @Size(max = 500, message = "Deletion reason cannot exceed 500 characters")
    @Field("deletion_reason")
    private String deletionReason;

    /**
     * Method to add a like from a user
     */
    public void addLike(Long userId) {
        if (this.likedByUsers == null) {
            this.likedByUsers = new HashSet<>();
        }
        if (this.likedByUsers.add(userId)) {
            this.likeCount = this.likedByUsers.size();
        }
    }

    /**
     * Method to remove a like from a user
     */
    public void removeLike(Long userId) {
        if (this.likedByUsers != null && this.likedByUsers.remove(userId)) {
            this.likeCount = this.likedByUsers.size();
        }
    }

    /**
     * Check if user has liked this comment
     */
    public boolean isLikedByUser(Long userId) {
        return this.likedByUsers != null && this.likedByUsers.contains(userId);
    }
}