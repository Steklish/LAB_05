package com.translator.translator.model.translation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    // JPQL query to fetch info with a condition. 
    @Query("SELECT t FROM Translation t WHERE t.user.id = :userId")
    List<Translation> findByUserId(@Param("userId") Long userId);
}
