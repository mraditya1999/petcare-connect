package com.petconnect.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumCreateDTO {
    private String title;
    private String content;
    private List<String> tags;
}
