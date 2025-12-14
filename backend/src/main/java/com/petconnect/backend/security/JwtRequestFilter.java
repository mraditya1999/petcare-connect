package com.petconnect.backend.security;

import com.petconnect.backend.config.SecurityProperties;
import com.petconnect.backend.exceptions.JwtTokenException;
import com.petconnect.backend.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final SecurityProperties securityProperties;

    @Autowired
    public JwtRequestFilter(@Lazy AuthService authService, JwtUtil jwtUtil, SecurityProperties securityProperties) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.securityProperties = securityProperties;
    }

@Override
protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
        throws ServletException, IOException {

    final String uri = request.getRequestURI();
    final Set<String> exemptPaths = Set.copyOf(securityProperties.jwtExemptPaths());

    // Skip JWT authentication for exempt paths
    if (exemptPaths.contains(uri)) {
        chain.doFilter(request, response);
        return;
    }

    final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

    String username = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
        jwt = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            // Check expiration early
            if (jwtUtil.isExpiredSafe(jwt)) {
                logger.warn("JWT Token has expired");
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token has expired");
                return;
            }
            username = jwtUtil.extractUsername(jwt);
            logger.debug("JWT Token found in the header, username extracted: {}", username);
        } catch (JwtTokenException e) {
            logger.warn("Failed to extract username from JWT token: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception e) {
            logger.warn("Unexpected error extracting username from JWT token: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid authentication token");
            return;
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
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token validation failed");
                return;
            }
        } catch (JwtTokenException e) {
            logger.error("JWT token error for user: {}", username, e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Error loading user details for JWT authentication: {}", username, e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication failed");
            return;
        }
    }

    chain.doFilter(request, response);
}

private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
    response.setStatus(status.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(String.format("{\"message\":\"%s\"}", message));
    response.getWriter().flush();
}

}
