package com.translator.translator.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.translator.translator.model.translation.Translation;
import com.translator.translator.model.translation.TranslationRepository;
import com.translator.translator.model.user.User;

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
}