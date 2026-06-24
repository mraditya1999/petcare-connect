package com.spring.petcareConnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.petcareConnect.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"user"})
@Entity
@Table(name = "oauth_accounts",
        indexes = {
                @Index(name = "idx_oauth_user", columnList = "user_id"),
                @Index(name = "idx_oauth_provider_user", columnList = "provider,provider_user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_oauth_provider_user", columnNames = {"provider", "provider_user_id"})
        })
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @NotNull(message = "User reference cannot be null")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Provider cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuthProvider provider; // GOOGLE, GITHUB, MOBILE, LOCAL

    @NotBlank(message = "Provider user ID cannot be blank")
    @Size(max = 255, message = "Provider user ID cannot exceed 255 characters")
    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId; // Google sub / GitHub id / etc.

    @Lob
    @Column(name = "access_token")
    @JsonIgnore
    @ToString.Exclude
    private String accessToken;

    @Lob
    @Column(name = "refresh_token")
    @JsonIgnore
    @ToString.Exclude
    private String refreshToken;

    @Column(name = "token_expiry")
    @JsonIgnore
    private Instant tokenExpiry;

    @Column(name = "refresh_token_expiry")
    @JsonIgnore
    private Instant refreshTokenExpiry;

    @Column(nullable = false)
    private boolean tokenValid = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}