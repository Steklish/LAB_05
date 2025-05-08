package com.translator.translator.model.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// User Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);
    Optional<User> findByName(String name);
}