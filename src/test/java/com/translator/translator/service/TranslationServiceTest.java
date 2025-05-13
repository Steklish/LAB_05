package com.translator.translator.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.translator.translator.cache.TranslationCache;
import com.translator.translator.dto.request.BulkTranslationRequest;
import com.translator.translator.model.translation.Translation;
import com.translator.translator.model.translation.TranslationRepository;
import com.translator.translator.model.user.User;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private UserService userService;

    @Mock
    private TranslationCache translationCache;

    @InjectMocks
    private TranslationService translationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTranslation() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Translation translation = new Translation();
        translation.setOriginalText("Hello");
        translation.setTranslatedText("Hola");

        when(userService.getUserById(userId)).thenReturn(user);
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);

        Translation result = translationService.createTranslation(userId, translation);

        assertNotNull(result);
        assertEquals("Hola", result.getTranslatedText());
        verify(translationCache).put(translation);
        verify(translationCache).invalidateUserTranslations(userId);
    }

    @Test
    void testGetTranslationsByUserId() {
        Long userId = 1L;
        Translation translation = new Translation();
        translation.setId(1L);
        translation.setOriginalText("Hello");
        translation.setTranslatedText("Hola");

        when(translationCache.getUserTranslations(userId)).thenReturn(Optional.empty());
        when(translationRepository.findByUserId(userId)).thenReturn(Arrays.asList(translation));

        List<Translation> result = translationService.getTranslationsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hola", result.get(0).getTranslatedText());
        verify(translationCache).putUserTranslations(userId, result);
    }

    @Test
    void testGetTranslationById() {
        Long translationId = 1L;
        Translation translation = new Translation();
        translation.setId(translationId);
        translation.setOriginalText("Hello");
        translation.setTranslatedText("Hola");

        when(translationCache.get(translationId)).thenReturn(Optional.empty());
        when(translationRepository.findById(translationId)).thenReturn(Optional.of(translation));

        Translation result = translationService.getTranslationById(translationId);

        assertNotNull(result);
        assertEquals("Hola", result.getTranslatedText());
        verify(translationCache).put(translation);
    }

    @Test
    void testUpdateTranslation() {
        Long translationId = 1L;
        Translation existingTranslation = new Translation();
        existingTranslation.setId(translationId);
        existingTranslation.setOriginalText("Hello");

        Translation updatedDetails = new Translation();
        updatedDetails.setOriginalText("Hi");
        updatedDetails.setTranslatedText("Hola");

        when(translationRepository.findById(translationId)).thenReturn(Optional.of(existingTranslation));
        when(translationRepository.save(any(Translation.class))).thenReturn(updatedDetails);

        Translation result = translationService.updateTranslation(translationId, updatedDetails);

        assertNotNull(result);
        assertEquals("Hi", result.getOriginalText());
        assertEquals("Hola", result.getTranslatedText());
        verify(translationCache).put(updatedDetails);
    }

    @Test
    void testDeleteTranslation() {
        Long translationId = 1L;
        Translation translation = new Translation();
        translation.setId(translationId);
        User user = new User();
        user.setId(1L);
        translation.setUser(user);

        when(translationRepository.findById(translationId)).thenReturn(Optional.of(translation));

        translationService.deleteTranslation(translationId);

        verify(translationRepository).deleteById(translationId);
        verify(translationCache).invalidate(translationId);
        verify(translationCache).invalidateUserTranslations(user.getId());
    }

    @Test
    void testcreateBulk() {
        // Setup test data
        Long userId1 = 1L;
        Long userId2 = 2L;
        User user1 = new User();
        user1.setId(userId1);
        User user2 = new User();
        user2.setId(userId2);

        Translation translation1 = new Translation();
        translation1.setUser(user1);
        translation1.setOriginalText("Hello");
        translation1.setTranslatedText("Hola");
        translation1.setOriginalLanguage("en");
        translation1.setTranslationLanguage("es");

        Translation translation2 = new Translation();
        translation2.setUser(user2);
        translation2.setOriginalText("Goodbye");
        translation2.setTranslatedText("Adiós");
        translation2.setOriginalLanguage("en");
        translation2.setTranslationLanguage("es");

        Translation duplicateTranslation = new Translation();
        duplicateTranslation.setUser(user1);
        duplicateTranslation.setOriginalText("Hello");
        duplicateTranslation.setTranslatedText("Hola");
        duplicateTranslation.setOriginalLanguage("en");
        duplicateTranslation.setTranslationLanguage("es");

        BulkTranslationRequest request = new BulkTranslationRequest();
        request.setTranslations(Arrays.asList(translation1, translation2, duplicateTranslation));

        // Mock repository behavior
        when(userService.getUserById(userId1)).thenReturn(user1);
        when(userService.getUserById(userId2)).thenReturn(user2);
        when(translationRepository.existsByUserAndOriginalTextAndOriginalLanguageAndTranslationLanguage(
                user1, "Hello", "en", "es")).thenReturn(true); // Mark as duplicate
        when(translationRepository.existsByUserAndOriginalTextAndOriginalLanguageAndTranslationLanguage(
                user2, "Goodbye", "en", "es")).thenReturn(false); // Not a duplicate
        when(translationRepository.saveAll(any(List.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        List<Translation> result = translationService.createBulk(request);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size()); // Only one non-duplicate translation should be saved
        assertEquals("Goodbye", result.get(0).getOriginalText());
        
        // Verify cache interactions
        verify(translationCache).put(result.get(0));
        verify(translationCache).invalidateUserTranslations(userId2);
        
        // Verify no cache interaction for the duplicate
        verify(translationCache, never()).put(duplicateTranslation);
        verify(translationCache, never()).invalidateUserTranslations(userId1);
    }

    @Test
    void testcreateBulkWithNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(null);
        });
    }

    @Test
    void testcreateBulkWithNullTranslations() {
        BulkTranslationRequest request = new BulkTranslationRequest();
        request.setTranslations(null);
        
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(request);
        });
    }

    @Test
    void testcreateBulkWithInvalidUser() {
        BulkTranslationRequest request = new BulkTranslationRequest();
        Translation translation = new Translation();
        translation.setUser(new User()); // User with null ID
        request.setTranslations(Arrays.asList(translation));
        
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(request);
        });
    }

    @Test
    void testcreateBulkWithEmptyText() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        
        BulkTranslationRequest request = new BulkTranslationRequest();
        Translation translation = new Translation();
        translation.setUser(user);
        translation.setOriginalText(""); // Empty text
        translation.setTranslatedText("Hola");
        request.setTranslations(Arrays.asList(translation));
        
        when(userService.getUserById(userId)).thenReturn(user);
        
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(request);
        });
    }


}
