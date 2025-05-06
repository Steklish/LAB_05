package com.translator.translator.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.translator.translator.cache.UserCache;
import com.translator.translator.model.user.User;
import com.translator.translator.model.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserCache userCache;
    
    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        when(userRepository.save(user)).thenReturn(user);

        // When
        User result = userService.createUser(user);

        // Then
        assertEquals("Alice", result.getName());
        verify(userRepository).save(user);
        verify(userCache).put(user);
    }

    @Test
    void testUpdateUser() {
        // Given
        User existingUser = new User(1L, "OldName");
        User updatedDetails = new User();
        updatedDetails.setName("NewName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // When
        User updatedUser = userService.updateUser(1L, updatedDetails);

        // Then
        assertEquals("NewName", updatedUser.getName());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
        verify(userCache).put(existingUser);
        verify(userCache).invalidateAllUsers();
    }

    @Test
    void testDeleteUser() {
        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).deleteById(1L);
        verify(userCache).invalidate(1L);
        verify(userCache).invalidateAllUsers();
    }

    @Test
    void testCreateUsersBulk() {
        // Given
        List<User> inputUsers = Arrays.asList(
                new User("Alice"),
                new User("Bob")
        );
        
        List<User> savedUsers = Arrays.asList(
                new User(1L, "Alice"),
                new User(2L, "Bob")
        );

        when(userRepository.saveAll(inputUsers)).thenReturn(savedUsers);

        // When
        List<User> result = userService.createUsersBulk(inputUsers);

        // Then
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
        
        verify(userRepository).saveAll(inputUsers);
        verify(userCache).putAllUsers(anyList());
        verify(userCache).invalidateAllUsers();
    }

    @Test
    void createUsersListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUsersBulk(null);
        });
    }

    @Test
    void createUsersListIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUsersBulk(List.of());
        });
    }


    @Test
    void deleteUsersDeleteAndInvalidateCache() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        
        // When
        userService.deleteUsersBulk(userIds);
        
        // Then
        verify(userRepository).deleteAllByIdInBatch(userIds);
        userIds.forEach(id -> verify(userCache).invalidate(id));
        verify(userCache).invalidateAllUsers();
    }

    @Test
    void deleteUsersListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUsersBulk(null);
        });
    }

    @Test
    void deleteUsersListIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUsersBulk(List.of());
        });
    }
}