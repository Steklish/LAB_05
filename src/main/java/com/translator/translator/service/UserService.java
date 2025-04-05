package com.translator.translator.service;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.translator.translator.model.user.User;
import com.translator.translator.model.user.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "allUsers", allEntries = true)
    public User createUser(User user) { 
        return userRepository.save(user); 
    }

    @Cacheable(value = "allUsers")
    public List<User> getAllUsers() { 
        return userRepository.findAll(); 
    }

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) { 
        return userRepository.findById(id).orElseThrow(); 
    }

    @CachePut(value = "users", key = "#id")
    @CacheEvict(value = "allUsers", allEntries = true)
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow();
        user.setName(userDetails.getName());
        return userRepository.save(user);
    }

    @CacheEvict(value = {"users", "allUsers"}, key = "#id")
    public void deleteUser(Long id) { 
        userRepository.deleteById(id); 
    }
}