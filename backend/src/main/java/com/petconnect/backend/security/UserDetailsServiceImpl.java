//package com.petconnect.backend.security;
//
//import com.petconnect.backend.entity.Role;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UserDetailsServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//
//        Set<GrantedAuthority> authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName().name())) // Ensure roleName is an enum
//                .collect(Collectors.toSet());
//
//        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
//    }
//}
