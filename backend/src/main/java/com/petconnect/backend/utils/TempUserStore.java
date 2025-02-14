package com.petconnect.backend.utils;

import com.petconnect.backend.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TempUserStore {
    private static final Logger logger = LoggerFactory.getLogger(TempUserStore.class);
    private final ConcurrentHashMap<String, User> tempUserStore = new ConcurrentHashMap<>();

    public void saveTemporaryUser(String token, User user) {
        tempUserStore.put(token, user);
        logger.info("Temporary user saved with token: {}", token);
    }

    public User getTemporaryUser(String token) {
        User user = tempUserStore.remove(token);
        if (user != null) {
            logger.info("Temporary user retrieved and removed with token: {}", token);
        } else {
            logger.warn("Temporary user not found or already removed with token: {}", token);
        }
        return user;
    }
}
