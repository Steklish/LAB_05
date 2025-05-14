package com.translator.translator.service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
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

    @Test
    void verifyAndTranslateBulk_success() throws JsonProcessingException {
        // Setup mock user
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);

        // Setup mock requests
        BulkTranslationItemRequest request1 = mock(BulkTranslationItemRequest.class);
        when(request1.getSourceLanguage()).thenReturn("fr");
        when(request1.getTargetLanguage()).thenReturn("en");
        when(request1.getOriginalText()).thenReturn("Bonjour");

        BulkTranslationItemRequest request2 = mock(BulkTranslationItemRequest.class);
        when(request2.getSourceLanguage()).thenReturn("es");
        when(request2.getTargetLanguage()).thenReturn("en");
        when(request2.getOriginalText()).thenReturn("Hola");

        List<BulkTranslationItemRequest> mockRequests = Arrays.asList(request1, request2);

        // Setup mock responses
        BulkTranslationItemResponse response1 = mock(BulkTranslationItemResponse.class);
        when(response1.getTranslatedLanguage()).thenReturn("fr");
        when(response1.getTranslatedLanguage()).thenReturn("en");
        when(response1.getOriginalText()).thenReturn("Bonjour");
        when(response1.getTranslatedText()).thenReturn("Translated: Bonjour [en]");
        when(response1.getTranslationId()).thenReturn(null);

        BulkTranslationItemResponse response2 = mock(BulkTranslationItemResponse.class);
        when(response2.getTranslatedLanguage()).thenReturn("es");
        when(response2.getTranslatedLanguage()).thenReturn("en");
        when(response2.getOriginalText()).thenReturn("Hola");
        when(response2.getTranslatedText()).thenReturn("Translated: Hola [en]");
        when(response2.getTranslationId()).thenReturn(null);

        List<BulkTranslationItemResponse> mockTranslatedResponses = Arrays.asList(response1, response2);

        // Setup mock saved translations
        Translation t1 = mock(Translation.class);
        when(t1.getId()).thenReturn(101L);
        when(t1.getUser()).thenReturn(mockUser);
        when(t1.getTranslatedText()).thenReturn("fr");
        when(t1.getTranslatedText()).thenReturn("en");
        when(t1.getOriginalText()).thenReturn("Bonjour");
        when(t1.getTranslatedText()).thenReturn("Translated: Bonjour [en]");

        Translation t2 = mock(Translation.class);
        when(t2.getId()).thenReturn(102L);
        when(t2.getUser()).thenReturn(mockUser);
        when(t2.getTranslatedText()).thenReturn("es");
        when(t2.getTranslatedText()).thenReturn("en");
        when(t2.getOriginalText()).thenReturn("Hola");
        when(t2.getTranslatedText()).thenReturn("Translated: Hola [en]");

        List<Translation> mockSavedTranslations = Arrays.asList(t1, t2);

        // Mock service calls
        when(userService.getUserById(1L)).thenReturn(mockUser);
        when(resolveQueryService.getBulkTranslations(mockRequests)).thenReturn(mockTranslatedResponses);
        ArgumentCaptor<List<Translation>> translationListCaptor = ArgumentCaptor.forClass(List.class);
        when(translationService.saveTranslations(translationListCaptor.capture())).thenReturn(mockSavedTranslations);

        // Execute test
        List<BulkTranslationItemResponse> result = authorizedTranslationService.verifyAndTranslateBulk(1L, mockRequests);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService, times(1)).getUserById(1L);
        verify(resolveQueryService, times(1)).getBulkTranslations(mockRequests);
        verify(translationService, times(1)).saveTranslations(anyList());
    }

    @Test
    void verifyAndTranslateBulk_userNotFound() throws JsonMappingException, JsonProcessingException {
        // Mock service call to throw exception
        when(userService.getUserById(999L)).thenThrow(new NoSuchElementException("User not found"));

        // Setup minimal mock requests
        BulkTranslationItemRequest request = mock(BulkTranslationItemRequest.class);
        List<BulkTranslationItemRequest> mockRequests = List.of(request);

        // Execute and verify
        assertThrows(NoSuchElementException.class,
                () -> authorizedTranslationService.verifyAndTranslateBulk(999L, mockRequests));

        verify(userService, times(1)).getUserById(999L);
        verify(resolveQueryService, never()).getBulkTranslations(anyList());
        verify(translationService, never()).saveTranslations(anyList());
    }

    @Test
    void verifyAndTranslateBulk_resolveQueryServiceThrowsJsonProcessingException() throws JsonProcessingException {
        // Setup mock user
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);

        // Setup minimal mock requests
        BulkTranslationItemRequest request = mock(BulkTranslationItemRequest.class);
        List<BulkTranslationItemRequest> mockRequests = List.of(request);

        // Mock service calls
        when(userService.getUserById(1L)).thenReturn(mockUser);
        when(resolveQueryService.getBulkTranslations(mockRequests))
                .thenThrow(mock(JsonProcessingException.class));

        // Execute and verify
        assertThrows(JsonProcessingException.class,
                () -> authorizedTranslationService.verifyAndTranslateBulk(1L, mockRequests));

        verify(userService, times(1)).getUserById(1L);
        verify(resolveQueryService, times(1)).getBulkTranslations(mockRequests);
        verify(translationService, never()).saveTranslations(anyList());
    }
}