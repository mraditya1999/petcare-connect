package com.petconnect.backend.security;

import com.petconnect.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailsServiceImpl implements UserDetails {

    private User user;

    public UserDetailsServiceImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> (GrantedAuthority) role::getAuthority)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Currently, User entity doesn't have account expiration functionality.
        // This can be extended in the future by adding an accountExpired field to User entity.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Currently, User entity doesn't have account lockout functionality.
        // This can be extended in the future by adding an accountLocked field to User entity.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Currently, User entity doesn't have password expiration functionality.
        // This can be extended in the future by adding a passwordExpiredAt field to User entity.
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isVerified();
    }

    // Additional getters for user details (if needed)
    public Long getUserId() {
        return user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public Set<String> getRoles() {
        return user.getRoles().stream()
                .map(role -> role.getRoleName().name()) // Assuming RoleName is an enum
                .collect(Collectors.toSet());
    }

    public User getUser() {
        return user;
    }
}