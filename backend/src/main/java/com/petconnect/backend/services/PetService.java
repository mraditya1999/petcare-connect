package com.petconnect.backend.services;

import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.repositories.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService
{
    @Autowired
    private PetRepository petRepository;

    public Pet createPet(Pet pet) {
        return petRepository.save(pet);
    }

    public List<Pet> getAllPets()
    {
        return petRepository.findAll();
    }


    public Pet getPetById(Long id)
    {
        return petRepository.findById(id).orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    public Pet updatePet(Long id, Pet pet)
    {
        Pet existingPet = getPetById(id);
        existingPet.setPetName(pet.getPetName());
        existingPet.setAge(pet.getAge());
        existingPet.setWeight(pet.getWeight());
        existingPet.setAvatarUrl(pet.getAvatarUrl());
        return petRepository.save(existingPet);
    }


    public void deletePet(Long id)
    {
        petRepository.deleteById(id);
    }

}
