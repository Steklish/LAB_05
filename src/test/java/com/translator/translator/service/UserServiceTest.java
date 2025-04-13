package com.translator.translator.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.translator.translator.cache.UserCache;
import com.translator.translator.model.user.User;
import com.translator.translator.model.user.UserRepository;

class UserServiceTest {

    private UserRepository userRepository;
    private UserCache userCache;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userCache = mock(UserCache.class);
        userService = new UserService(userRepository, userCache);
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("Alice", result.getName());
        verify(userRepository).save(user);
        verify(userCache).put(user);
    }



    @Test
    void testUpdateUser() {
        User existingUser = new User(1L, "OldName");
        User updatedDetails = new User();
        updatedDetails.setName("NewName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1L, updatedDetails);

        assertEquals("NewName", updatedUser.getName());
        verify(userRepository).save(existingUser);
        verify(userCache).put(existingUser);
        verify(userCache).invalidateAllUsers();
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
        verify(userCache).invalidate(1L);
        verify(userCache).invalidateAllUsers();
    }
}
