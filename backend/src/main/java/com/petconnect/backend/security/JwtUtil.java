package com.petconnect.backend.security;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.JwtTokenException;
import com.petconnect.backend.exceptions.JwtTokenExpiredException;
import com.petconnect.backend.exceptions.JwtTokenInvalidException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private Key secretKey;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @PostConstruct
    public void init() {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secret);
            this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid JWT secret key format. Must be base64 encoded.", e);
        }
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException();
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new JwtTokenInvalidException("Token is malformed or invalid");
        } catch (JwtException e) {
            throw new JwtTokenException("Failed to parse JWT token: " + e.getMessage());
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        return createToken(claims, subject, expiration * 1000);
    }

    private String createToken(Map<String, Object> claims, String subject, long ttlMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttlMillis))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createToken(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getAuthority).collect(Collectors.toList());
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", roles)
                .claim("userId", user.getUserId())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Generate token with custom TTL.
     */
    public String generateToken(Map<String, Object> claims, String subject, long ttlMillis) {
        return createToken(claims, subject, ttlMillis);
    }

    /**
     * Parse token safely with proper exception handling.
     */
    public Claims parseToken(String token) {
        return extractAllClaims(token);
    }

    /**
     * Safe expiry check (doesn't throw JwtException).
     */
    public boolean isExpiredSafe(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (JwtTokenException ex) {
            return true;
        }
    }
}
