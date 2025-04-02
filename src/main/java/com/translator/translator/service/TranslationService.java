package com.translator.translator.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.translator.translator.model.translation.Translation;
import com.translator.translator.model.translation.TranslationRepository;
import com.translator.translator.model.user.User;

// Translation Service
@Service
public class TranslationService {
    private final TranslationRepository translationRepository;
    private final UserService userService;

    public TranslationService(TranslationRepository translationRepository, UserService userService) {
        this.translationRepository = translationRepository;
        this.userService = userService;
    }

    public Translation createTranslation(Long userId, Translation translation) {
        User user = userService.getUserById(userId);
        translation.setUser(user);
        return translationRepository.save(translation);
    }

    public List<Translation> getTranslationsByUserId(Long userId) {
        return translationRepository.findByUserId(userId);
    }

    public Translation getTranslationById(Long id) { return translationRepository.findById(id).orElseThrow(); }
    public Translation updateTranslation(Long id, Translation translationDetails) {
        Translation translation = getTranslationById(id);
        // Update fields
        translation.setOriginalLanguage(translationDetails.getOriginalLanguage());
        translation.setTranslationLanguage(translationDetails.getTranslationLanguage());
        translation.setOriginalText(translationDetails.getOriginalText());
        translation.setTranslatedText(translationDetails.getTranslatedText());
        return translationRepository.save(translation);
    }
    public void deleteTranslation(Long id) { translationRepository.deleteById(id); }
}