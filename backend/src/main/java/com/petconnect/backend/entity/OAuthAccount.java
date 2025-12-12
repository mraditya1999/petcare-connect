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
@Builder
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
    @Column(name = "access_token", columnDefinition = "LONGTEXT")
    private String accessToken; // token may be long → LOB/Text

    @Lob
    @Column(name = "refresh_token", columnDefinition = "LONGTEXT")
    private String refreshToken;

    @Column(name = "token_expiry")
    private Instant tokenExpiry; // Instant = better for timezone handling

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

    public OAuthAccount() {
    }

    public OAuthAccount(Long id, User user, AuthProvider provider, String providerUserId, String accessToken, String refreshToken, Instant tokenExpiry, Instant refreshTokenExpiry, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiry = tokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Instant tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    public Instant getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Instant refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "OAuthAccount{" +
                "id=" + id +
                ", user=" + user +
                ", provider=" + provider +
                ", providerUserId='" + providerUserId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenExpiry=" + tokenExpiry +
                ", refreshTokenExpiry=" + refreshTokenExpiry +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
