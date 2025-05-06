package com.translator.translator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.translator.translator.dto.request.BulkTranslationRequest;
import com.translator.translator.model.translation.Translation;
import com.translator.translator.service.TranslationService;

import jakarta.validation.Valid;

// Translation Controller
@RestController
@RequestMapping("/translations")
public class TranslationController {
    
    @Autowired
    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Translation> createTranslation(
            @PathVariable Long userId,
            @Valid @RequestBody Translation translation) {
        return ResponseEntity.ok(translationService.createTranslation(userId, translation));
    }

    @GetMapping("/user/{userId}")
    public List<Translation> getTranslationsByUser(@PathVariable Long userId) {
        return translationService.getTranslationsByUserId(userId);
    }

    @GetMapping("/{id}")
    public Translation getTranslationById(@PathVariable Long id) {
        return translationService.getTranslationById(id);
    }

    @PutMapping("/{id}")
    public Translation updateTranslation(@PathVariable Long id, @Valid @RequestBody Translation translationDetails) {
        return translationService.updateTranslation(id, translationDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTranslation(@PathVariable Long id) {
        translationService.deleteTranslation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Translation>> createBulk(
        @Valid @RequestBody BulkTranslationRequest request) {
        return ResponseEntity.ok(translationService.createBulk(request));
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteUsersBulk(@RequestBody List<Long> translationIds) {
        translationService.deleteTranslationsBulk(translationIds);
        return ResponseEntity.noContent().build();
    }

}