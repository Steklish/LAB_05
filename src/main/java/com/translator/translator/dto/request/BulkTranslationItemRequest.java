package com.translator.translator.dto.request;

import jakarta.validation.constraints.NotBlank;

// DTO for a single item within a bulk translation request
public class BulkTranslationItemRequest {

    @NotBlank(message = "Source language cannot be blank")
    private String srcLan;

    @NotBlank(message = "Destination language cannot be blank")
    private String destLang;

    @NotBlank(message = "Text to translate cannot be blank")
    private String text;

    // Getters and Setters
    public String getSrcLan() {
        return srcLan;
    }

    public void setSrcLan(String srcLan) {
        this.srcLan = srcLan;
    }

    public String getDestLang() {
        return destLang;
    }

    public void setDestLang(String destLang) {
        this.destLang = destLang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Optional: Default constructor
    public BulkTranslationItemRequest() {}

    // Optional: Constructor with fields
    public BulkTranslationItemRequest(String srcLan, String destLang, String text) {
        this.srcLan = srcLan;
        this.destLang = destLang;
        this.text = text;
    }

    public String getSourceLanguage() {
        return this.srcLan;
    }

    public String getTargetLanguage() {
        return this.destLang;
    }

    public String getOriginalText() {
        return this.text;
    }
}