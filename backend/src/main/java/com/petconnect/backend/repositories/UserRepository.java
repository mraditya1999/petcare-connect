package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Find a user by verification token
    Optional<User> findByVerificationToken(String verificationToken);

    // Find a user by reset token
    Optional<User> findByResetToken(String resetToken);
}
