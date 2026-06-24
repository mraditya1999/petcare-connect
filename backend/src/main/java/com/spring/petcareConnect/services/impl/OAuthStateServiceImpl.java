package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.services.OAuthStateService;
import com.spring.petcareConnect.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OAuthStateServiceImpl implements OAuthStateService {
    private final RedisTemplate<String, String> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(OAuthStateServiceImpl.class);

    public OAuthStateServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // OAUTH STATE (simple mapping token -> present)
    private String oauthStateKey(String state) {
        return RedisUtils.oauthStateKey(state);
    }

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
