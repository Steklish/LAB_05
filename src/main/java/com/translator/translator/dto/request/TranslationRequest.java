package com.translator.translator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TranslationRequest {

    @NotBlank
    private String srcLan;

    @NotBlank
    private String destLang;

    @NotBlank
    private String text;

    @NotNull
    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
