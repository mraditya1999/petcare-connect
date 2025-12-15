package com.petconnect.backend.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"comments", "likes"})
@EqualsAndHashCode(exclude = {"comments", "likes"})
@Document(collection = "forums")
public class Forum {
    @Id
    private String forumId;

    @NotNull
    @Indexed
    private Long userId;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 200, max = 5000, message = "Content must be between 200 and 5000 characters")
    private String content;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Date updatedAt;

    // @DBRef
    private transient List<Comment> comments;

    // @DBRef
    private transient List<Like> likes;


    @Field("tags")
    @Size(min = 1, max = 5, message = "You must provide between 1 and 5 tags")
    private Set<
            @NotBlank(message = "Tag cannot be blank")
            @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Tags may only contain letters, numbers, hyphens, or underscores")
                    String
            > tags;

    // Custom setter for tags to ensure lowercase and filtering
    public void setTags(Collection<String> tags) {
        this.tags = tags != null ? tags.stream()
                .filter(Objects::nonNull)
                .map(tag -> tag.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet()) : null;
    }
}
