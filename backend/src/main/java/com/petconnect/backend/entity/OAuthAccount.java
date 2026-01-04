package com.petconnect.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "oauth_accounts",
        indexes = {
                @Index(name = "idx_oauth_user", columnList = "user_id"),
                @Index(name = "idx_oauth_provider_userId", columnList = "provider, providerUserId")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_oauth_provider_account",
                columnNames = {"provider", "providerUserId"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "accessToken", "refreshToken"})
@EqualsAndHashCode(exclude = {"user"})
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ----------------------------
    // USER RELATION
    // ----------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User reference cannot be null")
    private User user;

    // ----------------------------
    // PROVIDER DETAILS
    // ----------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "Provider cannot be null")
    private AuthProvider provider; // GOOGLE, GITHUB, etc.

    @NotBlank(message = "Provider user ID cannot be blank")
    @Size(max = 100, message = "Provider user ID cannot exceed 100 characters")
    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId; // Google sub / GitHub id

    // ----------------------------
    // TOKENS
    // ----------------------------
    @Lob
    @Column(name = "access_token")
    private String accessToken;

    @Lob
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_expiry")
    private Instant tokenExpiry;

    @Column(name = "refresh_token_expiry")
    private Instant refreshTokenExpiry;

    // ----------------------------
    // AUDIT FIELDS
    // ----------------------------

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum AuthProvider {
        GOOGLE,
        GITHUB,
        MOBILE,
        LOCAL
    }
}
