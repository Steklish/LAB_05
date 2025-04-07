package com.translator.translator.cache.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.translator.translator.cache.TranslationCache;
import com.translator.translator.model.translation.Translation;

@Component
public class InMemoryTranslationCache implements TranslationCache {
    private final Map<Long, Translation> translationCache = new ConcurrentHashMap<>();
    private final Map<Long, List<Translation>> userTranslationsCache = new ConcurrentHashMap<>();

    @Override
    public void put(Translation translation) {
        if (translation != null && translation.getId() != null) {
            translationCache.put(translation.getId(), translation);
        }
    }

    @Override
    public Optional<Translation> get(Long id) {
        return Optional.ofNullable(translationCache.get(id));
    }

    @Override
    public void invalidate(Long id) {
        translationCache.remove(id);
    }

    @Override
    public void invalidateAll() {
        translationCache.clear();
        userTranslationsCache.clear();
    }

    @Override
    public void putUserTranslations(Long userId, List<Translation> translations) {
        if (userId != null && translations != null) {
            userTranslationsCache.put(userId, List.copyOf(translations)); // Immutable copy
        }
    }

    @Override
    public Optional<List<Translation>> getUserTranslations(Long userId) {
        return Optional.ofNullable(userTranslationsCache.get(userId));
    }

    @Override
    public void invalidateUserTranslations(Long userId) {
        userTranslationsCache.remove(userId);
    }
}