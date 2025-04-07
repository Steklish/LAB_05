package com.translator.translator.dto.request;

import java.util.List;

import com.translator.translator.model.translation.Translation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class BulkTranslationRequest {
    @NotNull
    @Valid
    private List<Translation> translations;

    // getters and setters
    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }
}