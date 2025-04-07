package com.translator.translator.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.translator.translator.cache.UserCache;
import com.translator.translator.model.user.User;

@Component
public class InMemoryUserCache implements UserCache {
    private final Map<Long, User> userCache = new ConcurrentHashMap<>();
    private volatile List<User> allUsersCache = null;

    @Override
    public void put(User user) {
        if (user != null && user.getId() != null) {
            userCache.put(user.getId(), user);
            invalidateAllUsers(); // Invalidate the all-users cache when individual users change
        }
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(userCache.get(id));
    }

    @Override
    public void invalidate(Long id) {
        userCache.remove(id);
        invalidateAllUsers();
    }

    @Override
    public void invalidateAll() {
        userCache.clear();
        invalidateAllUsers();
    }

    @Override
    public void putAllUsers(List<User> users) {
        if (users != null) {
            // Cache individual users
            users.forEach(user -> {
                if (user.getId() != null) {
                    userCache.put(user.getId(), user);
                }
            });
            // Cache the complete list (immutable)
            this.allUsersCache = Collections.unmodifiableList(new ArrayList<>(users));
        }
    }

    @Override
    public Optional<List<User>> getAllUsers() {
        return Optional.ofNullable(allUsersCache);
    }

    @Override
    public void invalidateAllUsers() {
        this.allUsersCache = null;
    }
}