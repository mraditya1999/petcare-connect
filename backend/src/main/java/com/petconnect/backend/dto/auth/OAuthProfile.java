package com.petconnect.backend.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public class OAuthProfile {

    private final String providerUserId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String avatarUrl;

    public static OAuthProfile fromGoogle(GoogleUserDTO g) {
        if (!StringUtils.hasText(g.getSub())) {
            throw new IllegalStateException("Google user id (sub) is missing");
        }

        return new OAuthProfile(
                g.getSub(),
                g.getEmail(),
                normalize(g.getGiven_name()),
                normalize(g.getFamily_name()),
                g.getPicture()
        );
    }

    public static OAuthProfile fromGitHub(GitHubUserDTO gh) {
        if (gh.getId() == null) {
            throw new IllegalStateException("GitHub user id is missing");
        }

        String first = null;
        String last = null;

        if (StringUtils.hasText(gh.getName())) {
            String[] parts = gh.getName().trim().split("\\s+", 2);
            first = parts[0];
            if (parts.length > 1) {
                last = parts[1];
            }
        }

        return new OAuthProfile(
                String.valueOf(gh.getId()),
                gh.getEmail(),
                normalize(first),
                normalize(last),
                gh.getAvatar_url()
        );
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
