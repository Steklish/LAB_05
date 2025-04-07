package com.translator.translator.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.translator.translator.cache.TranslationCache;
import com.translator.translator.dto.request.BulkTranslationRequest;
import com.translator.translator.model.translation.Translation;
import com.translator.translator.model.translation.TranslationRepository;
import com.translator.translator.model.user.User;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class TranslationService {
    private final TranslationRepository translationRepository;
    private final UserService userService;
    private final TranslationCache translationCache;

    public TranslationService(TranslationRepository translationRepository, 
                            UserService userService,
                            TranslationCache translationCache) {
        this.translationRepository = translationRepository;
        this.userService = userService;
        this.translationCache = translationCache;
    }

    public Translation createTranslation(Long userId, Translation translation) {
        User user = userService.getUserById(userId);
        translation.setUser(user);
        Translation savedTranslation = translationRepository.save(translation);
        
        translationCache.put(savedTranslation);
        translationCache.invalidateUserTranslations(userId);
        
        return savedTranslation;
    }

    public List<Translation> getTranslationsByUserId(Long userId) {
        return translationCache.getUserTranslations(userId)
                .orElseGet(() -> {
                    List<Translation> translations = translationRepository.findByUserId(userId);
                    if (!translations.isEmpty()) {
                        translationCache.putUserTranslations(userId, translations);
                        translations.forEach(translationCache::put);
                    }
                    return translations;
                });
    }

    public Translation getTranslationById(Long id) { 
        return translationCache.get(id)
                .orElseGet(() -> {
                    Translation translation = translationRepository.findById(id).orElseThrow();
                    translationCache.put(translation);
                    return translation;
                });
    }

    public Translation updateTranslation(Long id, Translation translationDetails) {
        Translation translation = translationRepository.findById(id).orElseThrow();
        
        translation.setOriginalLanguage(translationDetails.getOriginalLanguage());
        translation.setTranslationLanguage(translationDetails.getTranslationLanguage());
        translation.setOriginalText(translationDetails.getOriginalText());
        translation.setTranslatedText(translationDetails.getTranslatedText());
        
        Translation updatedTranslation = translationRepository.save(translation);
        
        translationCache.put(updatedTranslation);
        if (updatedTranslation.getUser() != null) {
            translationCache.invalidateUserTranslations(updatedTranslation.getUser().getId());
        }
        
        return updatedTranslation;
    }

    public void deleteTranslation(Long id) {
        Translation translation = translationRepository.findById(id).orElseThrow();
        Long userId = translation.getUser() != null ? translation.getUser().getId() : null;
        
        translationRepository.deleteById(id);
        
        translationCache.invalidate(id);
        if (userId != null) {
            translationCache.invalidateUserTranslations(userId);
        }
    }


    @Transactional
    public List<Translation> processBulk(@Valid BulkTranslationRequest request) {
        // Validate request
        if (request == null || request.getTranslations() == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        // Extract and validate users
        Set<Long> userIds = request.getTranslations().stream()
                .map(t -> {
                    if (t.getUser() == null || t.getUser().getId() == null) {
                        throw new IllegalArgumentException("Translation must have a valid user ID");
                    }
                    return t.getUser().getId();
                })
                .collect(Collectors.toSet());

        // Verify all users exist
        Map<Long, User> userMap = userIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        userService::getUserById
                ));

        // Filter duplicates and validate translations
        List<Translation> uniqueTranslations = request.getTranslations().stream()
                .filter(t -> {
                    // Basic validation
                    if (t.getOriginalText() == null || t.getOriginalText().isBlank() ||
                        t.getTranslatedText() == null || t.getTranslatedText().isBlank()) {
                        throw new IllegalArgumentException("Original and translated text cannot be empty");
                    }

                    // Check for duplicates
                    return !translationRepository.existsByUserAndOriginalTextAndOriginalLanguageAndTranslationLanguage(
                            t.getUser(),
                            t.getOriginalText(),
                            t.getOriginalLanguage(),
                            t.getTranslationLanguage()
                    );
                })
                .peek(t -> {
                    // Ensure user reference is properly set
                    User user = userMap.get(t.getUser().getId());
                    t.setUser(user);
                })
                .collect(Collectors.toList());
        List<Translation> savedTranslations = translationRepository.saveAll(uniqueTranslations);

        savedTranslations.forEach(t -> {
            translationCache.put(t);
            translationCache.invalidateUserTranslations(t.getUser().getId());
        });

        // Batch save
        return translationRepository.saveAll(uniqueTranslations);
    }

}