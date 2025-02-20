package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    long countByIsVerified(boolean isVerified);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

        @Query("SELECT u FROM User u WHERE " +
                "u.firstName LIKE %:keyword% OR " +
                "u.lastName LIKE %:keyword% OR " +
                "u.email LIKE %:keyword% OR " +
                "u.mobileNumber LIKE %:keyword%")
        Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE LOWER(r.roleName) = LOWER(:roleName)")
    Page<User> findAllByRole(@Param("roleName") String roleName, Pageable pageable);
}
