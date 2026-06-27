package com.spring.petcareConnect.repositories.jpa;

import com.spring.petcareConnect.entities.Pet;
import com.spring.petcareConnect.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {
    Page<Pet> findAllByPetOwner(User user, Pageable pageable);

    Optional<Pet> findByPetIdAndPetOwner(Long petId, User petOwner);

    boolean existsByPetOwnerAndPetName(User petOwner, String name);
}
