package com.translator.translator.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.translator.translator.model.user.User;
import com.translator.translator.model.user.UserRepository;

// User Service
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) { return userRepository.save(user); }
    public List<User> getAllUsers() { return userRepository.findAll(); }
    public User getUserById(Long id) { return userRepository.findById(id).orElseThrow(); }
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        return userRepository.save(user);
    }
    public void deleteUser(Long id) { userRepository.deleteById(id); }
}