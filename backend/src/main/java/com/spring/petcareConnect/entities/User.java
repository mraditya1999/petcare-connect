package com.spring.petcareConnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.listeners.UserEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners(UserEntityListener.class)
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_mobile", columnList = "mobile_number")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long userId;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    @ToString.Include
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    @ToString.Include
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    @ToString.Include
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "address_id", nullable = true, unique = true)
    private Address address;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "avatar_public_id", length = 255)
    @JsonIgnore
    private String avatarPublicId;

    @Pattern(regexp = AppConstants.PHONE_REGEX, message = AppConstants.PHONE_VALIDATION_MESSAGE)
    @Column(name = "mobile_number", unique = true, length = 10)
    private String mobileNumber;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = AppConstants.PASSWORD_REGEX,
            message = AppConstants.PASSWORD_VALIDATION_MESSAGE)
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    @Column(name = "verification_token", length = 500)
    @JsonIgnore
    @ToString.Exclude
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    @JsonIgnore
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "refresh_token", length = 500)
    @JsonIgnore
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    @JsonIgnore
    private LocalDateTime refreshTokenExpiry;

    @Column(name = "reset_token", length = 500,unique = true)
    @JsonIgnore
    @ToString.Exclude
    private String resetToken;

    @Column(name = "reset_token_expiry")
    @JsonIgnore
    private LocalDateTime resetTokenExpiry;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private boolean profileComplete = false;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked = false;

    @ToString.Exclude
    @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<Pet> pets = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointments;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonIgnore
    private Set<OAuthAccount> oauthAccounts = new LinkedHashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @ToString.Include
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        email = email != null ? email.toLowerCase() : null;
    }

    @PreUpdate
    protected void onUpdate() {
        email = email != null ? email.toLowerCase() : null;
    }
}