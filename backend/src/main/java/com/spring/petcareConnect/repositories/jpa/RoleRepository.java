package com.spring.petcareConnect.repositories.jpa;

import com.spring.petcareConnect.entities.Role;
import com.spring.petcareConnect.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName roleName);
}