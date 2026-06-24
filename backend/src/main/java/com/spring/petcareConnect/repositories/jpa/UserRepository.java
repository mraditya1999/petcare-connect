package com.spring.petcareConnect.repositories.jpa;

import com.spring.petcareConnect.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByResetToken(String token);

    Optional<User> findByMobileNumber(String normalizedPhone);

    int countByVerified(boolean b);

    boolean existsByEmail(String email);
}
