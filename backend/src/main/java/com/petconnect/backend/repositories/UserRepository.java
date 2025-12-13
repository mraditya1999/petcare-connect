package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    long countByIsVerified(boolean isVerified);

    // Fetch user with roles + address (common for authentication/profile)
    @EntityGraph(value = "User.withRolesAndAddress", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByEmail(String email);

    // Fetch user with roles + address (profile view)
    @EntityGraph(value = "User.withRolesAndAddress", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findDetailedByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

        @Query("SELECT u FROM User u WHERE " +
                "u.firstName LIKE %:keyword% OR " +
                "u.lastName LIKE %:keyword% OR " +
                "u.email LIKE %:keyword% OR " +
                "u.mobileNumber LIKE %:keyword%")
        Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(value = "User.withRoles", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE LOWER(r.roleName) = LOWER(:roleName)")
    Page<User> findAllByRole(@Param("roleName") String roleName, Pageable pageable);

    @EntityGraph(value = "User.withRolesAndAddress", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByMobileNumber(String mobileNumber);
}
