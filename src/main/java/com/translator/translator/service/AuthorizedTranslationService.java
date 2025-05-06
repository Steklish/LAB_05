package com.translator.translator.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.translator.translator.dto.request.BulkTranslationItemRequest;
import com.translator.translator.dto.response.BulkTranslationItemResponse;
import com.translator.translator.model.translation.Translation;
import com.translator.translator.model.user.User;

@Service
public class AuthorizedTranslationService {

    
    @Autowired
    private final UserService userService;

    @Autowired
    private final TranslationService translationService;

    @Autowired
    private final ResolveQueryService resolveQueryService;

    public AuthorizedTranslationService(ResolveQueryService resolveQueryService, TranslationService translationService, UserService userService) {
        this.resolveQueryService = resolveQueryService;
        this.translationService = translationService;
        this.userService = userService;
    }

    


    public List<String> verifyTranslate( String srcLan, 
            String destLang,  String text, long id) throws JsonProcessingException {
        try {
            //!tst comment
            Optional.ofNullable(userService.getUserById(id));
            List<String> result = resolveQueryService.getTranslation(srcLan, destLang, text);
            Translation newTranslation = new Translation(srcLan, destLang, text, result.get(0));
            translationService.createTranslation(id, newTranslation);
            return result;
        } catch (NoSuchElementException e) {
            return List.of("error", "NO USER FOUND");
        }
    }
    

    public List<BulkTranslationItemResponse> verifyAndTranslateBulk(long userId, List<BulkTranslationItemRequest> requests)
        throws JsonProcessingException, JsonMappingException, NoSuchElementException { // Declare potential exceptions

        if (requests == null || requests.isEmpty()) {
            return List.of(); // Nothing to translate
        }

        // 1. Verify user exists
        // getUserById throws NoSuchElementException if not found.
        User user = userService.getUserById(userId);

        // 2. Get bulk translations from ResolveQueryService
        List<BulkTranslationItemResponse> translatedItems = resolveQueryService.getBulkTranslations(requests);

        List<Translation> translationsToSave = translatedItems.stream()
                .map(item -> {
                    Translation t = new Translation();
                    t.setUser(user); // Associate with the verified user
                    t.setOriginalLanguage(item.getOriginalLanguage());
                    t.setTranslationLanguage(item.getTranslatedLanguage());
                    t.setOriginalText(item.getOriginalText());
                    t.setTranslatedText(item.getTranslatedText());
                    return t;
                })
                .collect(Collectors.toList());

        List<Translation> savedTranslations = translationService.saveTranslations(translationsToSave);

        if (savedTranslations.size() == translatedItems.size()) {
            for (int i = 0; i < savedTranslations.size(); i++) {
                translatedItems.get(i).setTranslationId(savedTranslations.get(i).getId());
            }
        }
        return translatedItems;
    }

}
