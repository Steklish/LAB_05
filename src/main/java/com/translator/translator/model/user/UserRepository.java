package com.translator.translator.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

// User Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

