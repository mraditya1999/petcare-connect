package com.spring.petcareConnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.petcareConnect.enums.ForumTag;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "forums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tags"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@CompoundIndexes({
        @CompoundIndex(name = "idx_user_published", def = "{'user_id': 1, 'published': 1}"),
        @CompoundIndex(name = "idx_published_date", def = "{'published': 1, 'created_at': -1}"),
        @CompoundIndex(name = "idx_tags", def = "{'tags': 1}")
})
public class Forum {
    @EqualsAndHashCode.Include
    @Id
    private String forumId;

    @NotNull(message = "Creator user ID is required")
    @Positive(message = "User ID must be positive")
    @Indexed
    @Field("user_id")
    private Long userId;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    @Indexed
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 20, max = 5000, message = "Content must be between 20 and 5000 characters")
    private String content;

    @Size(min = 1, max = 10, message = "You must provide between 1 and 10 tags")
    @Field("tags")
    private Set<String> tags = new HashSet<>();

    @Field("published")
    private Boolean published = true;

    @Field("is_deleted")
    private Boolean isDeleted = false;

    @Size(max = 500, message = "Deletion reason cannot exceed 500 characters")
    @Field("deletion_reason")
    private String deletionReason;

    @Field("comment_count")
    private Integer commentCount = 0;

    @Field("like_count")
    private Integer likeCount = 0;

    @Field("liked_by_users")
    @JsonIgnore
    private Set<Long> likedByUsers = new HashSet<>();

    @Field("is_pinned")
    private Boolean isPinned = false;

    @Field("is_closed")
    private Boolean isClosed = false;

    @Field("view_count")
    private Long viewCount = 0L;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("is_locked")
    private Boolean isLocked = false;

    @Field("is_featured")
    private Boolean isFeatured = false;

    @Field("category")
    private ForumTag category;

    /**
     * Custom setter for tags - enforces lowercase
     */
    public void setTags(Collection<String> tags) {
        if (tags != null) {
            this.tags = tags.stream()
                    .filter(Objects::nonNull)
                    .filter(tag -> !tag.trim().isEmpty())
                    .map(tag -> tag.toLowerCase(Locale.ROOT).trim())
                    .limit(10)
                    .collect(Collectors.toSet());
        } else {
            this.tags = new HashSet<>();
        }
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount != null ? this.viewCount : 0L) + 1;
    }

    public void addLike(Long userId) {
        if (this.likedByUsers == null) {
            this.likedByUsers = new HashSet<>();
        }
        if (this.likedByUsers.add(userId)) {
            this.likeCount = this.likedByUsers.size();
        }
    }

    public void removeLike(Long userId) {
        if (this.likedByUsers != null && this.likedByUsers.remove(userId)) {
            this.likeCount = this.likedByUsers.size();
        }
    }

    public boolean isLikedByUser(Long userId) {
        return this.likedByUsers != null && this.likedByUsers.contains(userId);
    }

    public boolean isVisible() {
        return Boolean.TRUE.equals(this.published) && Boolean.FALSE.equals(this.isDeleted);
    }
}