package com.translator.translator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.translator.translator.service.AuthorizedTranslationService;
import com.translator.translator.service.ResolveQueryService;
import com.translator.translator.service.UserService;



// General API controller
@RestController
@RequestMapping("/usage")
public class GeneralController {
    
    @Autowired
    private final AuthorizedTranslationService authorizedTranslationService;

    @Autowired
    private final ResolveQueryService resolveQueryService;

    public GeneralController(AuthorizedTranslationService authorizedTranslationService, ResolveQueryService resolveQueryService, UserService userService) {
        this.authorizedTranslationService = authorizedTranslationService;
        this.resolveQueryService = resolveQueryService;
    }


    @GetMapping("/unauthorized")
    public List<String> getUnauthorizedTranslation(@RequestParam String srcLan, 
            @RequestParam String destLang, @RequestParam String text) throws JsonMappingException, JsonProcessingException { 
        return this.resolveQueryService.getTranslation(srcLan, destLang, text);
    }

    @GetMapping("/info")
    public String getUnauthorizedTranslationInfo() { 
        return "http://localhost:8080/usage/unauthorized?srcLan=fr&destLang=en&text=Salut";
    }


    @PostMapping
    public List<String> authorizedTranslation(@RequestParam String srcLan, 
            @RequestParam String destLang, @RequestParam String text, @RequestBody long userId) throws JsonProcessingException {
        return authorizedTranslationService.verifyTranslate(srcLan, destLang, text, userId);
    }
    
}
