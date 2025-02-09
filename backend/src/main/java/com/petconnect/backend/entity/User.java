//package com.petconnect.backend.entity;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.Size;
//import lombok.*;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Builder
//@Entity
//@Table(name = "users")
//public class User implements UserDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long userId;
//
//    @NotEmpty(message = "First name is required")
//    @Size(max = 50, message = "First name cannot exceed 50 characters")
//    @Column(nullable = false, length = 50)
//    private String firstName;
//
//   @NotEmpty(message = "Last name is required")
//    @Size(max = 50, message = "Last name cannot exceed 50 characters")
//   @Column(nullable = false, length = 50)
//    private String lastName;
//
//    @NotEmpty(message = "Email is required")
//    @Email(message = "Email must be valid")
//    @Size(max = 100, message = "Email cannot exceed 100 characters")
//    @Column(nullable = false, unique = true, length = 100)
//    private String email; // User login email
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "address_id", referencedColumnName = "addressId")
//    @JsonManagedReference
//    private Address address; // New address field
//
//    @Column(length = 255) // Adjust length if needed
//    private String avatarUrl; // URL of the avatar image stored in Cloudinary
//
//    @Column(length = 255) // Adjust length if needed
//    private String avatarPublicId; // Public ID of the avatar image in Cloudinary
//
////    @NotEmpty(message = "Mobile number is required")
//    @Size(max = 10, message = "Mobile number cannot exceed 10 characters")
//    @Column(length = 10)
//    private String mobileNumber;
//
//    @NotEmpty(message = "Password is required")
//    @Size(min = 6, message = "Password must have at least 6 characters")
//    @Column(nullable = false)
//    private String password; // Hashed password (for traditional login)
//
//    @Column
//    private String verificationToken; // Token used for account verification
//
//    @Column
//    private String resetToken; // Token used for password reset
//
//
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "user_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id"))
//    @JsonManagedReference
//    private Set<Role> roles = new HashSet<>();
//
//    @Column(nullable = false)
//    private boolean isVerified = false; // Tracks whether the user's email is verified
//
//    @Column
//    private String oauthProvider; // OAuth provider name (e.g., "google", "facebook")
//
//    @Column
//    private String oauthProviderId; // ID received from the OAuth provider (e.g., Google ID)
//
//    @Column(nullable = false)
//    private boolean isTwoFactorEnabled; // Tracks whether user enabled 2FA
//
//    @Column(nullable = false, updatable = false)
//    private Date createdAt; // Record creation timestamp
//
//    @Column(nullable = false)
//    private Date updatedAt; // Last updated timestamp
//
//    @PrePersist
//    public void onPersist() {
//        this.createdAt = new Date();
//        this.updatedAt = new Date();
//        this.isVerified = false; // Default: Email is unverified initially.
//        this.isTwoFactorEnabled = false; // Default: 2FA is disabled.
//    }
//
//    @PreUpdate
//    public void onUpdate() {
//        this.updatedAt = new Date();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return roles.stream().map(role -> (GrantedAuthority) role).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return isVerified; // Return true if the user's email is verified
//    }
//
//    public User() {
//    }
//
//    public User(Long userId, String firstName, String lastName, String email, Address address, String avatarUrl, String avatarPublicId, String mobileNumber, String password, String verificationToken, String resetToken, Set<Role> roles, boolean isVerified, String oauthProvider, String oauthProviderId, boolean isTwoFactorEnabled, Date createdAt, Date updatedAt) {
//        this.userId = userId;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.address = address;
//        this.avatarUrl = avatarUrl;
//        this.avatarPublicId = avatarPublicId;
//        this.mobileNumber = mobileNumber;
//        this.password = password;
//        this.verificationToken = verificationToken;
//        this.resetToken = resetToken;
//        this.roles = roles;
//        this.isVerified = isVerified;
//        this.oauthProvider = oauthProvider;
//        this.oauthProviderId = oauthProviderId;
//        this.isTwoFactorEnabled = isTwoFactorEnabled;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//
//    public Address getAddress() {
//        return address;
//    }
//
//    public void setAddress(Address address) {
//        this.address = address;
//    }
//
//    public String getAvatarUrl() {
//        return avatarUrl;
//    }
//
//    public void setAvatarUrl(String avatarUrl) {
//        this.avatarUrl = avatarUrl;
//    }
//
//    public String getAvatarPublicId() {
//        return avatarPublicId;
//    }
//
//    public void setAvatarPublicId(String avatarPublicId) {
//        this.avatarPublicId = avatarPublicId;
//    }
//
//    public String getMobileNumber() {
//        return mobileNumber;
//    }
//
//    public void setMobileNumber(String mobileNumber) {
//        this.mobileNumber = mobileNumber;
//    }
//
//    // Custom methods for role handling and verification status
//    public boolean hasRole(Role.RoleName roleName) {
//        return roles.stream().anyMatch(role -> role.getAuthority().equals(roleName));
//    }
//
//    public boolean isAdmin() {
//        return hasRole(Role.RoleName.ADMIN);
//    }
//
//    public boolean isSpecialist() {
//        return hasRole(Role.RoleName.SPECIALIST);
//    }
//
//    public boolean isRegularUser() {
//        return hasRole(Role.RoleName.USER);
//    }
//
//    public boolean canAccessSpecialistFeatures() {
//        return isAdmin() || isSpecialist();
//    }
//
//    public boolean isVerifiedAndEnabled() {
//        return this.isVerified && this.isTwoFactorEnabled;
//    }
//
//    public void setVerificationToken(String verificationToken) {
//        this.verificationToken = verificationToken;
//    }
//
//    public void setResetToken(String resetToken) {
//        this.resetToken = resetToken;
//    }
//
//    public boolean updateVerificationStatus(boolean isVerified) {
//        this.isVerified = isVerified;
//        return this.isVerified;
//    }
//
//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getOauthProvider() {
//        return oauthProvider;
//    }
//
//    public void setOauthProvider(String oauthProvider) {
//        this.oauthProvider = oauthProvider;
//    }
//
//    public String getOauthProviderId() {
//        return oauthProviderId;
//    }
//
//    public void setOauthProviderId(String oauthProviderId) {
//        this.oauthProviderId = oauthProviderId;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getVerificationToken() {
//        return verificationToken;
//    }
//
//    public String getResetToken() {
//        return resetToken;
//    }
//
//    public Set<Role> getRoles() {
//        return roles;
//    }
//
//
//
//    public void setRoles(Set<Role> roles) {
//        this.roles = roles;
//    }
//
//    public boolean isVerified() {
//        return isVerified;
//    }
//
//    public void setVerified(boolean isVerified) {
//        this.isVerified = isVerified;
//    }
//
//    public boolean isTwoFactorEnabled() {
//        return isTwoFactorEnabled;
//    }
//
//    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
//        isTwoFactorEnabled = twoFactorEnabled;
//    }
//
//    public Date getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Date createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Date getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(Date updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "userId=" + userId +
//                ", firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                ", email='" + email + '\'' +
//                ", oauthProvider='" + oauthProvider + '\'' +
//                ", oauthProviderId='" + oauthProviderId + '\'' +
//                ", password='" + password + '\'' +
//                ", verificationToken='" + verificationToken + '\'' +
//                ", resetToken='" + resetToken + '\'' +
//                ", roles=" + roles +
//                ", isVerified=" + isVerified +
//                ", isTwoFactorEnabled=" + isTwoFactorEnabled +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
//                '}';
//    }
//}

package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Builder
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "addressId")
    @JsonManagedReference
    private Address address; // New address field

    @Column(length = 255) // Adjust length if needed
    private String avatarUrl; // URL of the avatar image stored in Cloudinary

    @Column(length = 255) // Adjust length if needed
    private String avatarPublicId; // Public ID of the avatar image in Cloudinary

//    @NotEmpty(message = "Mobile number is required")
    @Size(max = 10, message = "Mobile number cannot exceed 10 characters")
    @Column(length = 10)
    private String mobileNumber;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password must have at least 6 characters")
    @Column(nullable = false)
    private String password; // Hashed password (for traditional login)

    @Column
    private String verificationToken; // Token used for account verification

    @Column
    private String resetToken; // Token used for password reset


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private boolean isVerified = false; // Tracks whether the user's email is verified

    @Column
    private String oauthProvider; // OAuth provider name (e.g., "google", "facebook")

    @Column
    private String oauthProviderId; // ID received from the OAuth provider (e.g., Google ID)

    @Column(nullable = false)
    private boolean isTwoFactorEnabled; // Tracks whether user enabled 2FA

    @Column(nullable = false, updatable = false)
    private Date createdAt; // Record creation timestamp

    @Column(nullable = false)
    private Date updatedAt; // Last updated timestamp

    @PrePersist
    public void onPersist() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isVerified = false; // Default: Email is unverified initially.
        this.isTwoFactorEnabled = false; // Default: 2FA is disabled.
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> (GrantedAuthority) role).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified; // Return true if the user's email is verified
    }

    public User() {
    }

    public User(Long userId, String firstName, String lastName, String email, Address address, String avatarUrl, String avatarPublicId, String mobileNumber, String password, String verificationToken, String resetToken, Set<Role> roles, boolean isVerified, String oauthProvider, String oauthProviderId, boolean isTwoFactorEnabled, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.avatarPublicId = avatarPublicId;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.verificationToken = verificationToken;
        this.resetToken = resetToken;
        this.roles = roles;
        this.isVerified = isVerified;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarPublicId() {
        return avatarPublicId;
    }

    public void setAvatarPublicId(String avatarPublicId) {
        this.avatarPublicId = avatarPublicId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    // Custom methods for role handling and verification status
    public boolean hasRole(Role.RoleName roleName) {
        return roles.stream().anyMatch(role -> role.getAuthority().equals(roleName));
    }

    public boolean isAdmin() {
        return hasRole(Role.RoleName.ADMIN);
    }

    public boolean isSpecialist() {
        return hasRole(Role.RoleName.SPECIALIST);
    }

    public boolean isRegularUser() {
        return hasRole(Role.RoleName.USER);
    }

    public boolean canAccessSpecialistFeatures() {
        return isAdmin() || isSpecialist();
    }

    public boolean isVerifiedAndEnabled() {
        return this.isVerified && this.isTwoFactorEnabled;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public boolean updateVerificationStatus(boolean isVerified) {
        this.isVerified = isVerified;
        return this.isVerified;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getOauthProviderId() {
        return oauthProviderId;
    }

    public void setOauthProviderId(String oauthProviderId) {
        this.oauthProviderId = oauthProviderId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public Set<Role> getRoles() {
        return roles;
    }



    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public boolean isTwoFactorEnabled() {
        return isTwoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        isTwoFactorEnabled = twoFactorEnabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", oauthProvider='" + oauthProvider + '\'' +
                ", oauthProviderId='" + oauthProviderId + '\'' +
                ", password='" + password + '\'' +
                ", verificationToken='" + verificationToken + '\'' +
                ", resetToken='" + resetToken + '\'' +
                ", roles=" + roles +
                ", isVerified=" + isVerified +
                ", isTwoFactorEnabled=" + isTwoFactorEnabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
     // Ensures correct serialization // <-- Use the exact field name in Pet
    private List<Pet> pets;

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

}
