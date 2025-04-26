package com.translator.translator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.translator.translator.model.user.User;
import com.translator.translator.service.UserService;

import jakarta.validation.Valid;

// User Controller
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        return userService.updateUser(id, userDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/bulk")   
    public ResponseEntity<List<User>> createUsersBulk(@RequestBody List<User> users) {
        List<User> createdUsers = userService.createUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteUsersBulk(@RequestBody List<Long> userIds) {
        userService.deleteUsers(userIds);
        return ResponseEntity.noContent().build();
    }
}
