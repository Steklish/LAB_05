package com.translator.translator.model.translation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.translator.translator.model.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Translation Entity
@Entity
@Table(name = "translations")
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_language", nullable = false)
    private String originalLanguage;

    @Column(name = "translation_language", nullable = false)
    private String translationLanguage;

    @Column(name = "original_text", nullable = false, length = 1000)
    private String originalText;

    @Column(name = "translated_text", nullable = false, length = 1000)
    private String translatedText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    // Constructors, getters, setters
    public Translation() {}
    // Parameterized constructor without user
    public Translation(String ol, String tl, String ot, String tt) {
        this.originalLanguage = ol;
        this.translationLanguage = tl;
        this.originalText = ot;
        this.translatedText = tt;
    }
    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOriginalLanguage() {
        return originalLanguage;
    }
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }
    public String getTranslationLanguage() {
        return translationLanguage;
    }
    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
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
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
