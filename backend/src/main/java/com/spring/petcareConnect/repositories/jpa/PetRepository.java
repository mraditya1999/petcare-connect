package com.spring.petcareConnect.repositories.jpa;

import com.spring.petcareConnect.entities.Pet;
import com.spring.petcareConnect.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {
    List<Pet> findAllByPetOwner(User user);
}
