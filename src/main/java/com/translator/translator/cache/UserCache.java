package com.translator.translator.cache;

import com.translator.translator.model.user.User;
import java.util.List;
import java.util.Optional;

public interface UserCache {
    void put(User user);
    Optional<User> get(Long id);
    void invalidate(Long id);
    void invalidateAll();
    
    void putAllUsers(List<User> users);
    Optional<List<User>> getAllUsers();
    void invalidateAllUsers();
}