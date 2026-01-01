    package com.petconnect.backend.entity;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.validation.constraints.*;
    import lombok.*;
    import lombok.experimental.SuperBuilder;
    import org.hibernate.annotations.BatchSize;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;
    import jakarta.persistence.*;

    import java.util.*;

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = {"password", "verificationToken", "resetToken", "pets", "appointments", "oauthAccounts"})
    @EqualsAndHashCode(exclude = {"pets", "appointments", "oauthAccounts", "roles"})
    @Entity
    @Table(name = "users")
    @Inheritance(strategy = InheritanceType.JOINED)
    @NamedEntityGraphs({
            @NamedEntityGraph(
                    name = "User.withRoles",
                    attributeNodes = {
                            @NamedAttributeNode("roles")
                    }
            ),
            @NamedEntityGraph(
                    name = "User.withRolesAndAddress",
                    attributeNodes = {
                            @NamedAttributeNode("roles"),
                            @NamedAttributeNode("address")
                    }
            ),
            @NamedEntityGraph(
                    name = "User.withOAuthAccounts",
                    attributeNodes = {
                            @NamedAttributeNode("oauthAccounts")
                    }
            )
    })
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long userId;

        @NotEmpty(message = "First name is required")
        @Size(max = 50, message = "First name cannot exceed 50 characters")
        @Column(nullable = true, length = 50)
        private String firstName;

        @NotEmpty(message = "Last name is required")
        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        @Column(nullable = true, length = 50)
        private String lastName;

        @NotEmpty(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        @Column(nullable = true, unique = true, length = 100)
        private String email;

        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "address_id", referencedColumnName = "addressId")
        @JsonManagedReference
        private Address address;

        @Column(length = 255)
        private String avatarUrl;

        @Column(length = 255)
        private String avatarPublicId;

        @Size(min = 10, max = 20, message = "Mobile number must be between 10 and 20 characters")
        @Column(length = 20, unique = true, nullable = true)
        private String mobileNumber;

//        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
//        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
//                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        @Column(nullable = true)
        private String password;

        @Column
        private String verificationToken;

        @Column(name = "reset_token")
        private String resetToken;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
        @JsonManagedReference
        @BatchSize(size = 20)
        private Set<Role> roles = new HashSet<>();

        @Column(nullable = false)
        private boolean verified = false;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private Date createdAt;

        @UpdateTimestamp
        @Column(nullable = false)
        private Date updatedAt;

        @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        @JsonManagedReference
        @BatchSize(size = 20)
        private List<Pet> pets;

        @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        @JsonManagedReference
        @BatchSize(size = 20)
        private List<Appointment> appointments;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
        @BatchSize(size = 20)
        private List<OAuthAccount> oauthAccounts = new ArrayList<>();

        @Column(nullable = false)
        private boolean profileComplete = false;

        // Custom setter for email to ensure lowercase
        public void setEmail(String email) {
            this.email = email != null ? email.toLowerCase(Locale.ROOT) : null;
        }
    }
