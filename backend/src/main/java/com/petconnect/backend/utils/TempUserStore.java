package com.petconnect.backend.utils;

import com.petconnect.backend.dto.auth.TempUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TempUserStore {

    private static final Logger logger = LoggerFactory.getLogger(TempUserStore.class);
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TempUserStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String token) {
        return "tempUser:" + token;
    }

    // Save temporary DTO with TTL (e.g. 24h)
    public void saveTemporaryUser(String token, TempUserDTO tempUserDTO, Duration ttl) {
        redisTemplate.opsForValue().set(key(token), tempUserDTO, ttl);
        logger.info("Temporary user DTO saved in Redis with token: {}", token);
    }

    // Retrieve and remove DTO (single-use)
    public TempUserDTO getTemporaryUser(String token) {
        Object o = redisTemplate.opsForValue().get(key(token));
        if (o == null) {
            logger.warn("Temporary user DTO not found or expired with token: {}", token);
            return null;
        }
        redisTemplate.delete(key(token));
        try {
            TempUserDTO dto = (TempUserDTO) o;
            logger.info("Temporary user DTO retrieved and removed with token: {}", token);
            return dto;
        } catch (ClassCastException ex) {
            logger.error("Unexpected type found in Redis for temp user key {}: {}", key(token), o.getClass().getName(), ex);
            redisTemplate.delete(key(token));
            return null;
        }
    }
}
