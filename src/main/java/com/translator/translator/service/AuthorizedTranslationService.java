package com.translator.translator.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.translator.translator.model.translation.Translation;

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
}
