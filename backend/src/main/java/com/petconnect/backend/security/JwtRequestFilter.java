package com.petconnect.backend.security;

import com.petconnect.backend.services.AuthService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    // Paths that skip JWT authentication (use temp tokens or no auth required)
    private static final Set<String> JWT_EXEMPT_PATHS = Set.of(
            "/api/v1/auth/complete-profile",
            "/api/v1/auth/verify-otp"
    );

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(@Lazy AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

@Override
protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
        throws ServletException, IOException {

    final String uri = request.getRequestURI();

    // Skip JWT authentication for exempt paths
    if (JWT_EXEMPT_PATHS.contains(uri)) {
        chain.doFilter(request, response);
        return;
    }

    final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

    String username = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
        jwt = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            username = jwtUtil.extractUsername(jwt);
            logger.debug("JWT Token found in the header, username extracted: {}", username);
        } catch (Exception e) {
            logger.warn("Failed to extract username from JWT token: {}", e.getMessage());
        }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        try {
            UserDetails userDetails = this.authService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("JWT Token validated, authentication set for user: {}", username);
            } else {
                logger.warn("JWT Token validation failed for user: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error loading user details for JWT authentication: {}", username, e);
        }
    }

    try {
        chain.doFilter(request, response);
    } catch (Exception e) {
        logger.error("Error occurred during JWT authentication", e);
        throw e;
    }
}

}
