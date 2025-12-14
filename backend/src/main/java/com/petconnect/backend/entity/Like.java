package com.petconnect.backend.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"forumPost"})
@EqualsAndHashCode(of = {"likeId"})
@Document(collection = "likes")
public class Like {
    @Id
    private String likeId;

    @NotNull
    @Indexed
    private String forumId;

    @NotNull
    @Indexed
    private String commentId;  

    @NotNull
    @Indexed
    private Long userId;

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    // @DBRef
    private transient Forum forumPost;
}
