package com.spring.petcareConnect.security.service;

import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("Username must not be empty");
        }

        String normalizedUsername = username.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + normalizedUsername));
        return UserDetailsImpl.build(user);
    }
}
