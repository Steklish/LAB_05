package com.translator.translator.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.translator.translator.dto.request.BulkTranslationItemRequest;
import com.translator.translator.dto.response.BulkTranslationItemResponse;
import com.translator.translator.service.AuthorizedTranslationService;
import com.translator.translator.service.ResolveQueryService;
import com.translator.translator.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;



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
    public List<String> getUnauthorizedTranslation(@Valid @RequestParam String srcLan, 
            @Valid @RequestParam String destLang, @Valid @RequestParam String text) throws JsonMappingException, JsonProcessingException { 
        return this.resolveQueryService.getTranslation(srcLan, destLang, text);
    }

    @GetMapping("/info")
    public String getUnauthorizedTranslationInfo() {
        return "Single unauthorized: GET http://localhost:8080/usage/unauthorized?srcLan=fr&destLang=en&text=Salut\n" +
               "Bulk unauthorized: POST http://localhost:8080/usage/unauthorized/bulk with body: [ { \"srcLan\": \"fr\", \"destLang\": \"en\", \"text\": \"Salut\" }, { \"srcLan\": \"es\", \"destLang\": \"en\", \"text\": \"Hola\" } ]\n" +
               "Single authorized: POST http://localhost:8080/usage/authorized?userId=123&srcLan=de&destLang=en&text=Hallo\n" +
               "Bulk authorized: POST http://localhost:8080/usage/authorized/bulk?userId=123 with body: [ { \"srcLan\": \"fr\", \"destLang\": \"en\", \"text\": \"Bonjour\" }, { \"srcLan\": \"de\", \"destLang\": \"en\", \"text\": \"Hallo\" } ]";
    }


    @PostMapping("/authorized")
    public List<String> authorizedTranslation(@Valid @RequestParam String srcLan, 
    @Valid @RequestParam String destLang, @Valid @RequestParam String text, @RequestBody long userId) throws JsonProcessingException {
        return authorizedTranslationService.verifyTranslate(srcLan, destLang, text, userId);
    }

    @PostMapping("/authorized/bulk")
    public List<BulkTranslationItemResponse> authorizedBulkTranslation(
            @RequestParam @Min(1) long userId, 
            @Valid @RequestBody List<BulkTranslationItemRequest> requests) 
            throws JsonProcessingException, JsonMappingException { // Declare potential exceptions

        if (requests == null || requests.isEmpty()) {
            return List.of(); // Nothing to translate/save
        }

        try {
            List<BulkTranslationItemResponse> translatedItems = authorizedTranslationService.verifyAndTranslateBulk(userId, requests);
            return translatedItems;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId, e);
        }
    }

    @PostMapping("/unauthorized/bulk")
    public List<BulkTranslationItemResponse> getUnauthorizedBulkTranslation(
            @Valid @RequestBody List<BulkTranslationItemRequest> requests) 
            throws JsonMappingException, JsonProcessingException { 

        if (requests == null || requests.isEmpty()) {
             return List.of();
        }
        return this.resolveQueryService.getBulkTranslations(requests);
    }
    
}
