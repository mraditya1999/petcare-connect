package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.*;

@SuperBuilder
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
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
    private String email;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "addressId")
    @JsonManagedReference
    private Address address;

    @Column(length = 255)
    private String avatarUrl;

    @Column(length = 255)
    private String avatarPublicId;

    @Size(max = 10, message = "Mobile number cannot exceed 10 characters")
    @Column(length = 10)
    private String mobileNumber;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password must have at least 6 characters")
    @Column(nullable = false)
    private String password;

    @Column
    private String verificationToken;

    @Column
    private String resetToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();


    @Column(nullable = false)
    private boolean isVerified = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

//    @Column
//    private String oauthProvider;
//
//    @Column
//    private String oauthProviderId;

    @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Pet> pets;

//        @PrePersist
//        public void onPersist() {
//            this.isVerified = false;
//        }

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


    public User() {
    }

    public User(Long userId, String firstName, String lastName, String email, Address address, String avatarUrl, String avatarPublicId, String mobileNumber, String password, String verificationToken, String resetToken, Set<Role> roles, boolean isVerified, Date createdAt, Date updatedAt, List<Pet> pets) {
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pets = pets;
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

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
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

    public void setVerified(boolean verified) {
        isVerified = verified;
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

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", avatarPublicId='" + avatarPublicId + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", password='" + password + '\'' +
                ", verificationToken='" + verificationToken + '\'' +
                ", resetToken='" + resetToken + '\'' +
                ", roles=" + roles +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", pets=" + pets +
                '}';
    }
}

