package com.petconnect.backend.services;

import com.petconnect.backend.dto.TempUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

//@Service
//public class RedisStorageService {
//
//    private final RedisTemplate<String, String> redisTemplate;
//
//    @Autowired
//    public RedisStorageService(RedisTemplate<String, String> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    private String verificationKey(String token) { return "token:verify:" + token; }
//    private String resetKey(String token)        { return "token:reset:" + token; }
//
//    // Store verification token with TTL (e.g., 24 hours)
//    public void saveVerificationToken(String token, String email, Duration ttl) {
//        redisTemplate.opsForValue().set(verificationKey(token), email, ttl);
//    }
//
//    public String getVerificationUser(String token) {
//        return redisTemplate.opsForValue().get(verificationKey(token));
//    }
//
//    public void deleteVerificationToken(String token) {
//        redisTemplate.delete(verificationKey(token));
//    }
//
//    // Store reset token with TTL (e.g., 15 minutes)
//    public void saveResetToken(String token, String email, Duration ttl) {
//        redisTemplate.opsForValue().set(resetKey(token), email, ttl);
//    }
//
//    public String getResetUser(String token) {
//        return redisTemplate.opsForValue().get(resetKey(token));
//    }
//
//    public void deleteResetToken(String token) {
//        redisTemplate.delete(resetKey(token));
//    }
//}

@Service
public class RedisStorageService {

    private static final Logger logger = LoggerFactory.getLogger(RedisStorageService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisStorageService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String tempUserKey(String token) { return "user:temp:" + token; }
    private String verifyKey(String token)    { return "token:verify:" + token; }
    private String resetKey(String token)     { return "token:reset:" + token; }

    // TEMP USER
    public void saveTempUser(String token, TempUserDTO dto, Duration ttl) {
        redisTemplate.opsForValue().set(tempUserKey(token), dto, ttl);
        logger.debug("Saved temp user token={}", token);
    }

    public TempUserDTO getAndRemoveTempUser(String token) {
        String key = tempUserKey(token);
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) return null;
        redisTemplate.delete(key);
        return (TempUserDTO) o;
    }

    // VERIFICATION TOKEN (simple mapping token -> email)
    public void saveVerificationToken(String token, String email, Duration ttl) {
        redisTemplate.opsForValue().set(verifyKey(token), email, ttl);
    }

    public String getVerificationEmail(String token) {
        return (String) redisTemplate.opsForValue().get(verifyKey(token));
    }

    public void deleteVerificationToken(String token) {
        redisTemplate.delete(verifyKey(token));
    }

    // RESET TOKEN
    public void saveResetToken(String token, String email, Duration ttl) {
        redisTemplate.opsForValue().set(resetKey(token), email, ttl);
    }
    public String getResetEmail(String token) {
        return (String) redisTemplate.opsForValue().get(resetKey(token));
    }
    public void deleteResetToken(String token) {
        redisTemplate.delete(resetKey(token));
    }

    // OAUTH STATE (simple mapping token -> present)
    private String oauthStateKey(String state) { return "oauth:state:" + state; }

    public void saveOAuthState(String state, Duration ttl) {
        redisTemplate.opsForValue().set(oauthStateKey(state), "1", ttl);
        logger.debug("Saved oauth state token={}", state);
    }

    public String getOAuthState(String state) {
        return (String) redisTemplate.opsForValue().get(oauthStateKey(state));
    }

    public void deleteOAuthState(String state) {
        redisTemplate.delete(oauthStateKey(state));
    }
}
