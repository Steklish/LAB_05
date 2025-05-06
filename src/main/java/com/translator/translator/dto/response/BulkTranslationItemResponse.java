package com.translator.translator.dto.response;

// DTO for a single item within a bulk translation response
public class BulkTranslationItemResponse {

    private String originalLanguage;
    private String translatedLanguage; 
    private String originalText;
    private String translatedText;
    private Long translationId;


    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTranslatedLanguage() {
        return translatedLanguage;
    }

    public void setTranslatedLanguage(String translatedLanguage) {
        this.translatedLanguage = translatedLanguage;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public Long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(Long translationId) {
        this.translationId = translationId;
    }

    public BulkTranslationItemResponse() {}

    public BulkTranslationItemResponse(String originalLanguage, String translatedLanguage, String originalText, String translatedText, Long translationId) {
        this.originalLanguage = originalLanguage;
        this.translatedLanguage = translatedLanguage;
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.translationId = translationId;
    }
}