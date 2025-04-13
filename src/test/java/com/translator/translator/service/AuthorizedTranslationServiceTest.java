package com.translator.translator.service;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;

class AuthorizedTranslationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TranslationService translationService;

    @Mock
    private ResolveQueryService resolveQueryService;

    @InjectMocks
    private AuthorizedTranslationService authorizedTranslationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testVerifyTranslateNoUserFound() throws JsonProcessingException {
        String srcLang = "en";
        String destLang = "es";
        String text = "Hello";
        long userId = 1L;

        when(userService.getUserById(userId)).thenThrow(new NoSuchElementException());

        List<String> result = authorizedTranslationService.verifyTranslate(srcLang, destLang, text, userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("error", result.get(0));
        assertEquals("NO USER FOUND", result.get(1));
        verify(userService).getUserById(userId);
        verifyNoInteractions(resolveQueryService);
        verifyNoInteractions(translationService);
    }
}
