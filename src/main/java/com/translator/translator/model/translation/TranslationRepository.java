package com.translator.translator.model.translation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// Translation Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByUserId(Long userId);
}