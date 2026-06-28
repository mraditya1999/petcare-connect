package com.spring.petcareConnect.dtos.forum.request;

import com.spring.petcareConnect.enums.ForumTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumCreateRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 20, max = 5000, message = "Content must be between 20 and 5000 characters")
    private String content;

    private ForumTag category;

    @Size(min = 1, max = 10, message = "You must provide between 1 and 10 tags")
    private Set<@Size(min = 2, max = 30, message = "Tag length must be between 2 and 30 characters") String> tags;
}
