package com.sashurman.userservice.repository;

import com.sashurman.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);
}
