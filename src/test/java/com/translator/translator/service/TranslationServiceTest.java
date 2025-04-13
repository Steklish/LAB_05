package com.translator.translator.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.translator.translator.cache.TranslationCache;
import com.translator.translator.model.translation.Translation;
import com.translator.translator.model.translation.TranslationRepository;
import com.translator.translator.model.user.User;

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
}
