package com.spring.petcareConnect.repositories.jpa;

import com.spring.petcareConnect.entities.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    Optional<Specialist> findByUserUserId(Long userId);
}
