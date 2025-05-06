package com.translator.translator.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.translator.translator.cache.UserCache;
import com.translator.translator.model.user.User;
import com.translator.translator.model.user.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserCache userCache;

    public UserService(UserRepository userRepository, UserCache userCache) {
        this.userRepository = userRepository;
        this.userCache = userCache;
    }

    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        userCache.put(savedUser);
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userCache.getAllUsers()
                .orElseGet(() -> {
                    List<User> users = userRepository.findAll();
                    if (!users.isEmpty()) {
                        userCache.putAllUsers(users);
                    }
                    return users;
                });
    }

    public User getUserById(Long id) {
        return userCache.get(id)
                .orElseGet(() -> {
                    User user = userRepository.findById(id).orElseThrow();
                    userCache.put(user);
                    return user;
                });
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow();
        user.setName(userDetails.getName());
        User updatedUser = userRepository.save(user);
        
        userCache.put(updatedUser);
        userCache.invalidateAllUsers();
        
        return updatedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        userCache.invalidate(id);
        userCache.invalidateAllUsers();
    }

    public List<User> createUsersBulk(List<User> users) {
        Assert.notNull(users, "Users list cannot be null");
        Assert.notEmpty(users, "Users list cannot be empty");
        
        List<User> savedUsers = userRepository.saveAll(users);
        userCache.putAllUsers(users);
        userCache.invalidateAllUsers();
        return savedUsers;
    }

    public void deleteUsersBulk(List<Long> userIds) {
        Assert.notNull(userIds, "User IDs list cannot be null");
        Assert.notEmpty(userIds, "User IDs list cannot be empty");
        
        // Delete all users in a single batch
        userRepository.deleteAllByIdInBatch(userIds);
        
        // Invalidate cache for all deleted users
        userIds.forEach(userCache::invalidate);
        
        // Invalidate the all-users cache
        userCache.invalidateAllUsers();
    }
}