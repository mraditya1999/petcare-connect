package com.spring.petcareConnect.services;

import com.spring.petcareConnect.utils.RedisUtils;

import java.time.Duration;

public interface OAuthStateService {
    private String oauthStateKey(String state) {
        return RedisUtils.oauthStateKey(state);
    }

    void saveOAuthState(String state, Duration ttl);

    String getOAuthState(String state);

    void deleteOAuthState(String state);

}
