package com.translator.translator.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.translator.translator.model.translation.Translation;

@Component
public class TranslationCache {
    private final Map<Long, List<Translation>> cache = new ConcurrentHashMap<>();

    public List<Translation> getCachedTranslations(Long userId) {
        return cache.get(userId);
    }

    public void putTranslations(Long userId, List<Translation> translations) {
        cache.put(userId, translations);
    }

    public void clearCache(Long userId) {
        cache.remove(userId);
    }
}
