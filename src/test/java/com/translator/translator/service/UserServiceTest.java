package com.translator.translator.service;

import java.util.Arrays;
import java.util.List;

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

    @Mock
    private User mockUser1;

    @Mock
    private User mockUser2;

    @Test
    void testCreateUser() {         
        when(mockUser1.getName()).thenReturn("Alice");
        when(userRepository.save(mockUser1)).thenReturn(mockUser1);

        User result = userService.createUser(mockUser1);

        // Then
        assertEquals("Alice", result.getName());
        verify(userRepository).save(mockUser1);
        verify(userCache).put(mockUser1);
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
         
        List<User> inputUsers = Arrays.asList(mockUser1, mockUser2);
        List<User> savedUsers = Arrays.asList(mockUser1, mockUser2);

        when(mockUser1.getName()).thenReturn("Alice");
        when(mockUser2.getName()).thenReturn("Bob");

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
