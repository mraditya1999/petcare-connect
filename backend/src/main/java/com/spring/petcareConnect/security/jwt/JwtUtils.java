package com.spring.petcareConnect.security.jwt;

import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.utils.PhoneUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.spring.petcareConnect.config.AppConstants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.jwt.expirationInSeconds}")
    private long jwtExpirationInSeconds;

    public String getJwtFromHeaders(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Generic token generator with purpose claim
    public String generateToken(String subject, String purpose, long expirationSeconds) {
        return Jwts.builder()
                .subject(subject)
                .claim("purpose", purpose)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .signWith(key())
                .compact();
    }

    // Specialized helpers
    public String generateLoginToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), AppConstants.TOKEN_PURPOSE_LOGIN, jwtExpirationInSeconds);
    }

    // In JwtUtils
    public boolean validateTempTokenForPhone(String token, String phone) {
        try {
            Claims claims = parseToken(token);

            // Check purpose claim
            if (!AppConstants.TEMP_TOKEN_PURPOSE_PROFILE_COMPLETION
                    .equals(claims.get("purpose", String.class))) {
                return false;
            }

            // Check phone claim (normalize to E.164 for consistency)
            String tokenPhone = claims.get("phone", String.class);
            if (!PhoneUtils.toIndianE164(phone).equals(tokenPhone)) {
                return false;
            }

            // Check expiry
            return !isTokenExpired(token);
        } catch (JwtException ex) {
            logger.warn("Temp token validation failed: {}", ex.getMessage());
            return false;
        }
    }


    public String generateEmailVerificationToken(String email, long expirationSeconds) {
        return generateToken(email, AppConstants.TOKEN_PURPOSE_EMAIL_VERIFICATION, expirationSeconds);
    }

    public String generateResetPasswordToken(String email, long expirationSeconds) {
        return generateToken(email, AppConstants.TOKEN_PURPOSE_RESET_PASSWORD, expirationSeconds);
    }

    // Validate token structure and signature
    public boolean validateJwtToken(String authToken) {
        try {
            parseToken(authToken); // will throw if invalid
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("JWT token is expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("JWT token is unsupported: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    // Extract username (subject) from token
    public String getUsernameFromJwtToken(String token) {
        return parseToken(token).getSubject();
    }


    public String generateTempTokenForPhone(String phone, String purpose, long expirationSeconds) {
        return Jwts.builder()
                .claim("phone", phone)
                .claim("purpose", purpose)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .signWith(key())
                .compact();
    }

    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000))
                .signWith(key())
                .compact();
    }


    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateTokenPurpose(String token, String expectedPurpose) {
        try {
            Claims claims = parseToken(token);
            return expectedPurpose.equals(claims.get("purpose", String.class)) && !isTokenExpired(token);
        } catch (JwtException ex) {
            logger.warn("Token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public Date getExpirationDateFromJwtToken(String token) {
        return parseToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDateFromJwtToken(token).before(new Date());
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateTokenFromUser(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail() == null ? user.getMobileNumber() : user.getEmail())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities(user.getRoles().stream().map(r -> r.getRoleName().name()).toArray(String[]::new))
                .build();
        return generateLoginToken(userDetails);
    }


}
