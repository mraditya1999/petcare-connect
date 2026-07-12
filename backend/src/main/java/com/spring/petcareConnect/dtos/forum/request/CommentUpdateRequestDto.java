package com.spring.petcareConnect.dtos.forum.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequestDto {

    @NotBlank(message = "Updated comment text is required")
    @Size(min = 2, max = 2000, message = "Comment must be between 2 and 2000 characters")
    private String text;

    private String deletionReason;
}
