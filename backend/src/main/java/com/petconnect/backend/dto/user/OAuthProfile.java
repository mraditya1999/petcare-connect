package com.petconnect.backend.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuthProfile {
    private final String providerUserId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String avatarUrl;

    public static OAuthProfile fromGoogle(GoogleUserDTO g) {
        String first = g.getGiven_name();
        String last = g.getFamily_name();
        return new OAuthProfile(g.getSub(), g.getEmail(), first, last, g.getPicture());
    }

    public static OAuthProfile fromGitHub(GitHubUserDTO gh) {
        String first = null, last = null;
        if (gh.getName() != null) {
            String[] parts = gh.getName().split(" ", 2);
            first = parts[0];
            if (parts.length > 1) last = parts[1];
        }
        return new OAuthProfile(String.valueOf(gh.getId()), gh.getEmail(), first, last, gh.getAvatar_url());
    }
}
