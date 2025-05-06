package com.translator.translator.service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.translator.translator.dto.request.BulkTranslationItemRequest;
import com.translator.translator.dto.response.BulkTranslationItemResponse;
import com.translator.translator.model.translation.Translation;
import com.translator.translator.model.user.User;

@ExtendWith(MockitoExtension.class)
class AuthorizedTranslationServiceTests {

    @InjectMocks
    private AuthorizedTranslationService authorizedTranslationService;

    @Mock
    private UserService userService;

    @Mock
    private TranslationService translationService;

    @Mock
    private ResolveQueryService resolveQueryService;

    private User mockUser;
    private List<BulkTranslationItemRequest> mockRequests;
    private List<BulkTranslationItemResponse> mockTranslatedResponses;
    private List<Translation> mockSavedTranslations; 

    @BeforeEach
    void setUp() {
        mockUser = new User(1L, "Test User");

        mockRequests = Arrays.asList(
                new BulkTranslationItemRequest("fr", "en", "Bonjour"),
                new BulkTranslationItemRequest("es", "en", "Hola")
        );

        // Simulate responses from ResolveQueryService
        mockTranslatedResponses = Arrays.asList(
                new BulkTranslationItemResponse("fr", "en", "Bonjour", "Translated: Bonjour [en]", null), // IDs null initially
                new BulkTranslationItemResponse("es", "en", "Hola", "Translated: Hola [en]", null)
        );

        Translation t1 = new Translation("fr", "en", "Bonjour", "Translated: Bonjour [en]");
        t1.setId(101L);
        t1.setUser(mockUser);
        Translation t2 = new Translation("es", "en", "Hola", "Translated: Hola [en]");
        t2.setId(102L);
        t2.setUser(mockUser);
        mockSavedTranslations = Arrays.asList(t1, t2);
    }

    @Test
    void verifyAndTranslateBulk_success() throws JsonProcessingException, JsonMappingException {
        long userId = mockUser.getId();

       
        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(resolveQueryService.getBulkTranslations(mockRequests)).thenReturn(mockTranslatedResponses);
        ArgumentCaptor<List<Translation>> translationListCaptor = ArgumentCaptor.forClass(List.class);
        when(translationService.saveTranslations(translationListCaptor.capture())).thenReturn(mockSavedTranslations); 
        // When
        List<BulkTranslationItemResponse> result = authorizedTranslationService.verifyAndTranslateBulk(userId, mockRequests);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Bonjour", result.get(0).getOriginalText());
        assertEquals("Translated: Bonjour [en]", result.get(0).getTranslatedText());
      
        assertEquals(mockSavedTranslations.get(0).getId(), result.get(0).getTranslationId());

        assertEquals("Hola", result.get(1).getOriginalText());
        assertEquals("Translated: Hola [en]", result.get(1).getTranslatedText());
        assertEquals(mockSavedTranslations.get(1).getId(), result.get(1).getTranslationId());


        verify(userService, times(1)).getUserById(userId);
        verify(resolveQueryService, times(1)).getBulkTranslations(mockRequests);
        verify(translationService, times(1)).saveTranslations(anyList()); 

        List<Translation> capturedTranslations = translationListCaptor.getValue();
        assertNotNull(capturedTranslations);
        assertEquals(2, capturedTranslations.size());
        
        assertEquals(mockUser, capturedTranslations.get(0).getUser());
        assertEquals("Bonjour", capturedTranslations.get(0).getOriginalText());
        assertEquals("Translated: Bonjour [en]", capturedTranslations.get(0).getTranslatedText());
        assertNull(capturedTranslations.get(0).getId()); 

        assertEquals(mockUser, capturedTranslations.get(1).getUser());
        assertEquals("Hola", capturedTranslations.get(1).getOriginalText());
        assertEquals("Translated: Hola [en]", capturedTranslations.get(1).getTranslatedText());
        assertNull(capturedTranslations.get(1).getId()); 
    }

    @Test
    void verifyAndTranslateBulk_userNotFound() throws JsonProcessingException, JsonMappingException {
        long userId = 999L; // Non-existent ID

        when(userService.getUserById(userId)).thenThrow(new NoSuchElementException("User not found"));

        assertThrows(NoSuchElementException.class,
                     () -> authorizedTranslationService.verifyAndTranslateBulk(userId, mockRequests));

        verify(userService, times(1)).getUserById(userId);
        verify(resolveQueryService, never()).getBulkTranslations(anyList());
        verify(translationService, never()).saveTranslations(anyList());
    }



    @Test
    void verifyAndTranslateBulk_resolveQueryServiceThrowsJsonProcessingException() throws JsonProcessingException, JsonMappingException {
        long userId = mockUser.getId();

        // Given
        when(userService.getUserById(userId)).thenReturn(mockUser);
        
        when(resolveQueryService.getBulkTranslations(mockRequests))
                .thenThrow(new JsonProcessingException("Resolve error") {});

        assertThrows(JsonProcessingException.class,
                () -> authorizedTranslationService.verifyAndTranslateBulk(userId, mockRequests));

        
        verify(userService, times(1)).getUserById(userId);
        verify(resolveQueryService, times(1)).getBulkTranslations(mockRequests);
        
        verify(translationService, never()).saveTranslations(anyList());
    }

}