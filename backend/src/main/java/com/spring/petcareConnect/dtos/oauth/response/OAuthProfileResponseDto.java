package com.spring.petcareConnect.dtos.oauth.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
public class OAuthProfileResponseDto {

    @NotBlank(message = "providerUserId is required")
    private final String providerUserId;

    @Email(message = "email must be a valid email address")
    private final String email;

    @Size(max = 100)
    private final String firstName;

    @Size(max = 100)
    private final String lastName;

    private final String avatarUrl;

    public static OAuthProfileResponseDto fromGoogle(GoogleUserResponseDto g) {
        if (!StringUtils.hasText(g.getSub())) {
            throw new IllegalStateException("Google user id (sub) is missing");
        }

        return new OAuthProfileResponseDto(
                g.getSub(),
                g.getEmail(),
                normalize(g.getGiven_name()),
                normalize(g.getFamily_name()),
                g.getPicture()
        );
    }

    public static OAuthProfileResponseDto fromGitHub(GitHubUserResponseDto gh) {
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

        return new OAuthProfileResponseDto(
                String.valueOf(gh.getId()),
                gh.getEmail(),
                normalize(first),
                normalize(last),
                gh.getAvatar_url()
        );
    }

    public static OAuthProfileResponseDto fromMobile(String mobileNumber) {
        if (!StringUtils.hasText(mobileNumber)) {
            throw new IllegalStateException("Mobile number is missing for OAuthProfileResponseDto");
        }
        return new OAuthProfileResponseDto(
                mobileNumber, // providerUserId
                null,         // email
                null,         // firstName
                null,         // lastName
                null          // avatarUrl
        );
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
