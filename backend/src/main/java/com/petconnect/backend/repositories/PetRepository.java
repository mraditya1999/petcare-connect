package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {
    List<Pet> findAllByPetOwner(User petOwner);
    boolean existsByPetOwnerAndPetName(User petOwner, String name);
}
