package com.epam.gym_crm.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    private final Cache<String, Long> blacklisted = Caffeine.newBuilder().build();

    public void blacklist(String token, long ttlSeconds) {
        if (ttlSeconds <= 0) return;
        long until = System.currentTimeMillis() + ttlSeconds * 1000L;
        blacklisted.put(token, until);
    }

    public boolean isBlacklisted(String token) {
        Long until = blacklisted.getIfPresent(token);
        if (until == null) return false;
        if (System.currentTimeMillis() >= until) {
            blacklisted.invalidate(token);
            return false;
        }
        return true;
    }
}
