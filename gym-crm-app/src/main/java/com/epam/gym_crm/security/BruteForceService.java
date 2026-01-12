package com.epam.gym_crm.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BruteForceService {

    //counter for unsuccessful attempts
    private final Cache<String, Integer> attempts =
            Caffeine.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .build();
    //five-minutes blocking
    private final Cache<String, Boolean> blocked =
            Caffeine.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .build();
    private static final int LIMIT = 3;
    public boolean isBlocked(String username) {
        Boolean b = blocked.getIfPresent(username);
        return b != null && b;
    }
    public void onSuccess(String username) {
        attempts.invalidate(username);
        blocked.invalidate(username);
    }
    public void onFailure(String username) {
        int cur = attempts.get(username, k -> 0) + 1;
        if (cur >= LIMIT) {
            blocked.put(username, Boolean.TRUE);
            attempts.invalidate(username);
        } else {
            attempts.put(username, cur);
        }
    }
    public int remaining(String username) {

        Integer cur = attempts.getIfPresent(username);
        int used = (cur == null) ? 0 : cur;
        return Math.max(0, LIMIT - used);
    }
}
