package com.petconnect.backend.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"parentComment", "replies"})
@EqualsAndHashCode(of = {"commentId"})
@Document(collection = "comments")
public class Comment {

    @Id
    private String commentId;

    @NotBlank(message = "Forum ID is required")
    @Indexed
    private String forumId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;


    @NotBlank(message = "Comment text is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Comment cannot be only whitespace")
    @Size(min = 2, max = 1000, message = "Comment must be between 2 and 1000 characters")
    private String text;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Date updatedAt;

    // @ElementCollection
    private Set<Long> likedByUsers = new HashSet<>();

    @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Parent ID must be a valid ObjectId")
    @Field("parent_id")
    private String parentId;

    // @ManyToOne
    // @JoinColumn(name = "parent_id", referencedColumnName = "commentId", insertable = false, updatable = false) // Join using parent_id
    private transient Comment parentComment;

    // @DBRef // Use DBRef for replies
    // @OneToMany(mappedBy = "parentComment") // Remove cascade here
    private transient Set<Comment> replies = new HashSet<>();
}
