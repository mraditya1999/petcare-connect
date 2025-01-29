package com.petconnect.backend.config;

import com.petconnect.backend.entity.User;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TempUserStore {
    private final ConcurrentHashMap<String, User> tempUserStore = new ConcurrentHashMap<>();

    public void saveTemporaryUser(String token, User user) {
        tempUserStore.put(token, user);
    }

    public User getTemporaryUser(String token) {
        return tempUserStore.remove(token);
    }
}
