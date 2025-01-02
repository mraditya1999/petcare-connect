package com.petconnect.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotEmpty(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotEmpty(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String email; // User login email

    @Column
    private String oauthProvider; // OAuth provider name (e.g., "google", "facebook")

    @Column
    private String oauthProviderId; // ID received from the OAuth provider (e.g., Google ID)

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password must have at least 6 characters")
    @Column(nullable = false)
    private String password; // Hashed password (for traditional login)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // User role: USER, SPECIALIST, ADMIN

    @Column(nullable = false)
    private boolean isVerified; // Tracks whether the user's email is verified

    @Column(nullable = false)
    private boolean isTwoFactorEnabled; // Tracks whether user enabled 2FA

    @Column(nullable = false, updatable = false)
    private Date createdAt; // Record creation timestamp

    @Column(nullable = false)
    private Date updatedAt; // Last updated timestamp

    /**
     * Automatically set createdAt and updatedAt values before persisting.
     */
    @PrePersist
    public void onPersist() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        if (this.role == null) {
            this.role = Role.USER; // Default role (USER) if not explicitly assigned.
        }
        this.isVerified = false; // Default: Email is unverified initially.
        this.isTwoFactorEnabled = false; // Default: 2FA is disabled.
    }

    /**
     * Automatically update updatedAt value before updating.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
    }

    public void setVerificationToken(Object o) {
    }

    public void setResetToken(String resetToken) {

    }

    /**
     * User Roles: USER, SPECIALIST, ADMIN
     */
    public enum Role {
        USER,       // Default role for standard users.
        SPECIALIST, // Specialist role for domain experts.
        ADMIN       // Administrator role with highest privileges.
    }

    // Additional methods, like custom logic with roles, can go here if needed in future.

    public boolean hasRole(Role role) {
        // Check if the user has the specified role
        return this.role.equals(role);
    }

    public boolean isAdmin() {
        // Shortcut method to check if the user is an ADMIN
        return this.role == Role.ADMIN;
    }

    public boolean isSpecialist() {
        // Shortcut method to check if the user is a SPECIALIST
        return this.role == Role.SPECIALIST;
    }

    public boolean isRegularUser() {
        // Shortcut method to check if the user is a regular USER
        return this.role == Role.USER;
    }

    public boolean canAccessSpecialistFeatures() {
        // Determine if the user has access to features intended for SPECIALIST or higher roles
        return this.role == Role.SPECIALIST || this.role == Role.ADMIN;
    }

    public boolean isVerifiedAndEnabled() {
        // Check if the user is verified and also has two-factor authentication enabled
        return this.isVerified && this.isTwoFactorEnabled;
    }

    public boolean updateVerificationStatus(boolean isVerified) {
        // Update the `isVerified` field for the user
        this.isVerified = isVerified;
        return this.isVerified; // Return the updated status
    }
}