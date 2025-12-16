package com.petconnect.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitHubUserDTO {
    private Long id;
    private String login;
    private String avatar_url;
    private String name;
    private String email;
}
