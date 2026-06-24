package com.spring.petcareConnect.dtos.oauth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GitHubUserResponseDto {
    private Long id;
    private String login;
    private String avatar_url;
    private String name;
    private String email;
}
