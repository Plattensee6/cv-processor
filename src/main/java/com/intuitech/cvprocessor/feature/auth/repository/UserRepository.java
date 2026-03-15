package com.intuitech.cvprocessor.feature.auth.repository;

import com.intuitech.cvprocessor.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetTokenAndPasswordResetExpiresAtAfter(String token, java.time.OffsetDateTime expiryThreshold);
}

