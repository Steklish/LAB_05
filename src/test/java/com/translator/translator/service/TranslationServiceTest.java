package com.translator.translator.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Test
    void testCreateTranslation() {
        Long userId = 1L;
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        Translation translation = mock(Translation.class);
        when(translation.getOriginalText()).thenReturn("Hello");
        when(translation.getTranslatedText()).thenReturn("Hola");

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
        Translation translation = mock(Translation.class);
        when(translation.getId()).thenReturn(1L);
        when(translation.getOriginalText()).thenReturn("Hello");
        when(translation.getTranslatedText()).thenReturn("Hola");

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
        Translation translation = mock(Translation.class);
        when(translation.getId()).thenReturn(translationId);
        when(translation.getOriginalText()).thenReturn("Hello");
        when(translation.getTranslatedText()).thenReturn("Hola");

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
        Translation existingTranslation = mock(Translation.class);
        when(existingTranslation.getId()).thenReturn(translationId);
        when(existingTranslation.getOriginalText()).thenReturn("Hello");

        Translation updatedDetails = mock(Translation.class);
        when(updatedDetails.getOriginalText()).thenReturn("Hi");
        when(updatedDetails.getTranslatedText()).thenReturn("Hola");

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
        Translation translation = mock(Translation.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(translation.getUser()).thenReturn(user);
        when(translation.getId()).thenReturn(translationId);

        when(translationRepository.findById(translationId)).thenReturn(Optional.of(translation));

        translationService.deleteTranslation(translationId);

        verify(translationRepository).deleteById(translationId);
        verify(translationCache).invalidate(translationId);
        verify(translationCache).invalidateUserTranslations(user.getId());
    }

    @Test
    void testCreateBulk() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(userId1);
        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(userId2);

        Translation translation1 = mock(Translation.class);
        when(translation1.getUser()).thenReturn(user1);
        when(translation1.getOriginalText()).thenReturn("Hello");
        when(translation1.getTranslatedText()).thenReturn("Hola");
        when(translation1.getOriginalLanguage()).thenReturn("en");
        when(translation1.getTranslationLanguage()).thenReturn("es");

        Translation translation2 = mock(Translation.class);
        when(translation2.getUser()).thenReturn(user2);
        when(translation2.getOriginalText()).thenReturn("Goodbye");
        when(translation2.getTranslatedText()).thenReturn("Adiós");
        when(translation2.getOriginalLanguage()).thenReturn("en");
        when(translation2.getTranslationLanguage()).thenReturn("es");

        Translation duplicateTranslation = mock(Translation.class);
        when(duplicateTranslation.getUser()).thenReturn(user1);
        when(duplicateTranslation.getOriginalText()).thenReturn("Hello");
        when(duplicateTranslation.getTranslatedText()).thenReturn("Hola");
        when(duplicateTranslation.getOriginalLanguage()).thenReturn("en");
        when(duplicateTranslation.getTranslationLanguage()).thenReturn("es");

        BulkTranslationRequest request = mock(BulkTranslationRequest.class);
        when(request.getTranslations()).thenReturn(Arrays.asList(translation1, translation2, duplicateTranslation));

        when(userService.getUserById(userId1)).thenReturn(user1);
        when(userService.getUserById(userId2)).thenReturn(user2);
        when(translationRepository.existsByUserAndOriginalTextAndOriginalLanguageAndTranslationLanguage(
                user1, "Hello", "en", "es")).thenReturn(true);
        when(translationRepository.existsByUserAndOriginalTextAndOriginalLanguageAndTranslationLanguage(
                user2, "Goodbye", "en", "es")).thenReturn(false);
        when(translationRepository.saveAll(any(List.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        List<Translation> result = translationService.createBulk(request);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Goodbye", result.get(0).getOriginalText());
        verify(translationCache).put(result.get(0));
        verify(translationCache).invalidateUserTranslations(userId2);
        verify(translationCache, never()).put(duplicateTranslation);
        verify(translationCache, never()).invalidateUserTranslations(userId1);
    }

    @Test
    void testCreateBulkWithNullRequest() {
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(null);
        });
    }

    @Test
    void testCreateBulkWithNullTranslations() {
        BulkTranslationRequest request = mock(BulkTranslationRequest.class);
        when(request.getTranslations()).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(request);
        });
    }

    @Test
    void testCreateBulkWithInvalidUser() {
        BulkTranslationRequest request = mock(BulkTranslationRequest.class);
        Translation translation = mock(Translation.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn(null);
        when(translation.getUser()).thenReturn(user);
        when(request.getTranslations()).thenReturn(List.of(translation));
        
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(request);
        });
    }

    @Test
    void testCreateBulkWithEmptyText() {
        Long userId = 1L;
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        
        BulkTranslationRequest request = mock(BulkTranslationRequest.class);
        Translation translation = mock(Translation.class);
        when(translation.getUser()).thenReturn(user);
        when(translation.getOriginalText()).thenReturn("");
        when(translation.getTranslatedText()).thenReturn("Hola");
        when(request.getTranslations()).thenReturn(List.of(translation));
        
        when(userService.getUserById(userId)).thenReturn(user);
        
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createBulk(request);
        });
    }
}