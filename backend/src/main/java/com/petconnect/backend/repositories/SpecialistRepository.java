package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Specialist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
        Page<Specialist> findByFirstNameContainingOrSpecialityContaining(String name, String speciality, Pageable pageable);
}
