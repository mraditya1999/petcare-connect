package com.petconnect.backend.services;

import com.petconnect.backend.dto.auth.TempUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisStorageService {

    private static final Logger logger = LoggerFactory.getLogger(RedisStorageService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisStorageService(RedisTemplate<String, Object> redisTemplate) {
        if (redisTemplate == null) {
            throw new IllegalArgumentException("RedisTemplate cannot be null");
        }
        this.redisTemplate = redisTemplate;
    }

    private String tempUserKey(String token) { 
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        return "user:temp:" + token; 
    }
    
    private String verifyKey(String token) { 
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        return "token:verify:" + token; 
    }
    
    private String resetKey(String token) { 
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        return "token:reset:" + token; 
    }

    // TEMP USER
    /**
     * Saves a temporary user to Redis with a TTL.
     *
     * @param token the token key (must not be null or blank)
     * @param dto the temporary user DTO (must not be null)
     * @param ttl the time to live duration (must not be null)
     * @throws IllegalArgumentException if any parameter is null or token is blank
     */
    public void saveTempUser(String token, TempUserDTO dto, Duration ttl) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        if (dto == null) {
            throw new IllegalArgumentException("TempUserDTO cannot be null");
        }
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        
        try {
            redisTemplate.opsForValue().set(tempUserKey(token), dto, ttl);
            logger.debug("Saved temp user token={}", token);
        } catch (Exception e) {
            logger.error("Error saving temp user with token: {}", token, e);
            throw new RuntimeException("Failed to save temp user", e);
        }
    }

    /**
     * Retrieves and removes a temporary user from Redis.
     *
     * @param token the token key (must not be null or blank)
     * @return the TempUserDTO if found, null otherwise
     * @throws IllegalArgumentException if token is null or blank
     */
    public TempUserDTO getAndRemoveTempUser(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        
        try {
            String key = tempUserKey(token);
            Object o = redisTemplate.opsForValue().get(key);
            if (o == null) {
                logger.debug("Temp user not found for token: {}", token);
                return null;
            }
            redisTemplate.delete(key);
            logger.debug("Retrieved and removed temp user for token: {}", token);
            return (TempUserDTO) o;
        } catch (Exception e) {
            logger.error("Error retrieving temp user with token: {}", token, e);
            throw new RuntimeException("Failed to retrieve temp user", e);
        }
    }

    // VERIFICATION TOKEN (simple mapping token -> email)
    /**
     * Saves a verification token to Redis with a TTL.
     *
     * @param token the verification token (must not be null or blank)
     * @param email the email address (must not be null or blank)
     * @param ttl the time to live duration (must not be null)
     * @throws IllegalArgumentException if any parameter is null or token/email is blank
     */
    public void saveVerificationToken(String token, String email, Duration ttl) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        
        try {
            redisTemplate.opsForValue().set(verifyKey(token), email, ttl);
            logger.debug("Saved verification token for email: {}", email);
        } catch (Exception e) {
            logger.error("Error saving verification token for email: {}", email, e);
            throw new RuntimeException("Failed to save verification token", e);
        }
    }

    /**
     * Retrieves the email associated with a verification token.
     *
     * @param token the verification token (must not be null or blank)
     * @return the email address if found, null otherwise
     * @throws IllegalArgumentException if token is null or blank
     */
    public String getVerificationEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        
        try {
            return (String) redisTemplate.opsForValue().get(verifyKey(token));
        } catch (Exception e) {
            logger.error("Error retrieving verification email for token: {}", token, e);
            throw new RuntimeException("Failed to retrieve verification email", e);
        }
    }

    /**
     * Deletes a verification token from Redis.
     *
     * @param token the verification token (must not be null or blank)
     * @throws IllegalArgumentException if token is null or blank
     */
    public void deleteVerificationToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        
        try {
            redisTemplate.delete(verifyKey(token));
            logger.debug("Deleted verification token: {}", token);
        } catch (Exception e) {
            logger.error("Error deleting verification token: {}", token, e);
            throw new RuntimeException("Failed to delete verification token", e);
        }
    }

    // RESET TOKEN
    /**
     * Saves a password reset token to Redis with a TTL.
     *
     * @param token the reset token (must not be null or blank)
     * @param email the email address (must not be null or blank)
     * @param ttl the time to live duration (must not be null)
     * @throws IllegalArgumentException if any parameter is null or token/email is blank
     */
    public void saveResetToken(String token, String email, Duration ttl) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        
        try {
            redisTemplate.opsForValue().set(resetKey(token), email, ttl);
            logger.debug("Saved reset token for email: {}", email);
        } catch (Exception e) {
            logger.error("Error saving reset token for email: {}", email, e);
            throw new RuntimeException("Failed to save reset token", e);
        }
    }
    
    /**
     * Retrieves the email associated with a reset token.
     *
     * @param token the reset token (must not be null or blank)
     * @return the email address if found, null otherwise
     * @throws IllegalArgumentException if token is null or blank
     */
    public String getResetEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        
        try {
            return (String) redisTemplate.opsForValue().get(resetKey(token));
        } catch (Exception e) {
            logger.error("Error retrieving reset email for token: {}", token, e);
            throw new RuntimeException("Failed to retrieve reset email", e);
        }
    }
    
    /**
     * Deletes a reset token from Redis.
     *
     * @param token the reset token (must not be null or blank)
     * @throws IllegalArgumentException if token is null or blank
     */
    public void deleteResetToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        
        try {
            redisTemplate.delete(resetKey(token));
            logger.debug("Deleted reset token: {}", token);
        } catch (Exception e) {
            logger.error("Error deleting reset token: {}", token, e);
            throw new RuntimeException("Failed to delete reset token", e);
        }
    }

    // OAUTH STATE (simple mapping token -> present)
    private String oauthStateKey(String state) { 
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or blank");
        }
        return "oauth:state:" + state; 
    }

    /**
     * Saves an OAuth state to Redis with a TTL.
     *
     * @param state the OAuth state (must not be null or blank)
     * @param ttl the time to live duration (must not be null)
     * @throws IllegalArgumentException if any parameter is null or state is blank
     */
    public void saveOAuthState(String state, Duration ttl) {
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or blank");
        }
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        
        try {
            redisTemplate.opsForValue().set(oauthStateKey(state), "1", ttl);
            logger.debug("Saved oauth state token={}", state);
        } catch (Exception e) {
            logger.error("Error saving OAuth state: {}", state, e);
            throw new RuntimeException("Failed to save OAuth state", e);
        }
    }

    /**
     * Retrieves an OAuth state from Redis.
     *
     * @param state the OAuth state (must not be null or blank)
     * @return "1" if found, null otherwise
     * @throws IllegalArgumentException if state is null or blank
     */
    public String getOAuthState(String state) {
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or blank");
        }
        
        try {
            return (String) redisTemplate.opsForValue().get(oauthStateKey(state));
        } catch (Exception e) {
            logger.error("Error retrieving OAuth state: {}", state, e);
            throw new RuntimeException("Failed to retrieve OAuth state", e);
        }
    }

    /**
     * Deletes an OAuth state from Redis.
     *
     * @param state the OAuth state (must not be null or blank)
     * @throws IllegalArgumentException if state is null or blank
     */
    public void deleteOAuthState(String state) {
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or blank");
        }
        
        try {
            redisTemplate.delete(oauthStateKey(state));
            logger.debug("Deleted OAuth state: {}", state);
        } catch (Exception e) {
            logger.error("Error deleting OAuth state: {}", state, e);
            throw new RuntimeException("Failed to delete OAuth state", e);
        }
    }
}
