package com.translator.translator.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    public TranslationService(TranslationRepository translationRepository, UserService userService) {
        this.translationRepository = translationRepository;
        this.userService = userService;
    }

    @CacheEvict(value = "userTranslations", key = "#userId")
    public Translation createTranslation(Long userId, Translation translation) {
        User user = userService.getUserById(userId);
        translation.setUser(user);
        return translationRepository.save(translation);
    }

    @Cacheable(value = "userTranslations", key = "#userId")
    public List<Translation> getTranslationsByUserId(Long userId) {
        return translationRepository.findByUserId(userId);
    }

    @Cacheable(value = "translations", key = "#id")
    public Translation getTranslationById(Long id) { 
        return translationRepository.findById(id).orElseThrow(); 
    }

    @CachePut(value = "translations", key = "#id")
    @CacheEvict(value = "userTranslations", allEntries = true)
    public Translation updateTranslation(Long id, Translation translationDetails) {
        Translation translation = translationRepository.findById(id).orElseThrow();
        // Update fields
        translation.setOriginalLanguage(translationDetails.getOriginalLanguage());
        translation.setTranslationLanguage(translationDetails.getTranslationLanguage());
        translation.setOriginalText(translationDetails.getOriginalText());
        translation.setTranslatedText(translationDetails.getTranslatedText());
        return translationRepository.save(translation);
    }

    @CacheEvict(value = {"translations", "userTranslations"}, key = "#id")
    public void deleteTranslation(Long id) {
        translationRepository.deleteById(id); 
    }

    @Transactional
    @CacheEvict(value = "userTranslations", allEntries = true)
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

        // Batch save
        return translationRepository.saveAll(uniqueTranslations);
    }

}