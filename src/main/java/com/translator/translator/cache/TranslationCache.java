package com.translator.translator.cache;

import java.util.List;
import java.util.Optional;

import com.translator.translator.model.translation.Translation;
public interface TranslationCache {
    void put(Translation translation);
    Optional<Translation> get(Long id);
    void invalidate(Long id);
    void invalidateAll();
    
    void putUserTranslations(Long userId, List<Translation> translations);
    Optional<List<Translation>> getUserTranslations(Long userId);
    void invalidateUserTranslations(Long userId);
}