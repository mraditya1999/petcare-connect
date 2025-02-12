//package com.petconnect.backend.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.util.Date;
//
//@Entity
//@Table(name = "oauth_tokens")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class OAuthToken {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user; // Associate token with the User entity
//
//    @Column(nullable = false)
//    private String provider; // e.g., "google", "facebook"
//
//    @Column(nullable = false, unique = true)
//    private String accessToken; // The OAuth2 access token
//
//    @Column
//    private String refreshToken; // The OAuth2 refresh token (if applicable)
//
//    @Column(nullable = false)
//    private Date tokenExpiry; // Expiration time for the access token
//
//    @Column
//    private Date refreshTokenExpiry; // (Optional) Expiration for the refresh token
//
//    @Column(nullable = false)
//    private Date createdAt;
//
//    @Column(nullable = false)
//    private Date updatedAt;
//
//    @PrePersist
//    protected void onPersist() {
//        createdAt = new Date();
//        updatedAt = new Date();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = new Date();
//    }
//}