package com.petconnect.backend.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateForumDTO {
    private String title;
    private String content;
    private List<String> tags;
}
