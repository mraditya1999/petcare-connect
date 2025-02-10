//package com.petconnect.backend.services;
//
//import com.petconnect.backend.dto.PetDTO;
//import com.petconnect.backend.entity.Pet;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.repositories.PetRepository;
//import com.petconnect.backend.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class PetService {
//
//    @Autowired
//    private PetRepository petRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Transactional
//    public Pet createPetForUser(PetDTO petRequest, String username) {
//        User petOwner = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Pet pet = new Pet();
//        pet.setPetName(petRequest.getPetName());
//        pet.setAge(petRequest.getAge());
//        pet.setWeight(petRequest.getWeight());
//        pet.setAvatarUrl(petRequest.getAvatarUrl());
//        pet.setPetOwner(petOwner);
//
//        return petRepository.save(pet);
//    }
//
//    public List<PetDTO> getAllPets() {
//        return petRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
//    }
//
//    @Transactional
//    public List<PetDTO> getAllPetsForUser(String username) {
//        User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return petRepository.findAllByPetOwner(user).stream().map(this::convertToDto).collect(Collectors.toList());
//    }
//
//    public Pet getPetById(Long id) {
//        return petRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Pet not found"));
//    }
//
//    @Transactional
//    public Pet updatePetForUser(Long id, Pet pet, String username) {
//        Pet existingPet = getPetById(id);
//        if (!existingPet.getPetOwner().getUsername().equals(username)) {
//            throw new RuntimeException("Unauthorized to update this pet");
//        }
//        existingPet.setPetName(pet.getPetName());
//        existingPet.setAge(pet.getAge());
//        existingPet.setWeight(pet.getWeight());
//        existingPet.setAvatarUrl(pet.getAvatarUrl());
//        existingPet.setAvatarPublicId(pet.getAvatarPublicId());
//        return petRepository.save(existingPet);
//    }
//
//    @Transactional
//    public void deletePetForUser(Long id, String username) {
//        Pet existingPet = getPetById(id);
//        if (!existingPet.getPetOwner().getUsername().equals(username)) {
//            throw new RuntimeException("Unauthorized to delete this pet");
//        }
//        petRepository.deleteById(id);
//    }
//
//    public PetDTO convertToDto(Pet pet) {
//        PetDTO petDTO = new PetDTO();
//        petDTO.setPetId(pet.getPetId());
//        petDTO.setPetName(pet.getPetName());
//        petDTO.setAge(pet.getAge());
//        petDTO.setWeight(pet.getWeight());
//        petDTO.setAvatarUrl(pet.getAvatarUrl());
//        petDTO.setOwnerFirstName(pet.getPetOwner().getFirstName());
//        petDTO.setOwnerLastName(pet.getPetOwner().getLastName());
//        petDTO.setOwnerMobileNumber(pet.getPetOwner().getMobileNumber());
//        return petDTO;
//    }
//}

package com.petconnect.backend.services;

import com.petconnect.backend.dto.PetDTO;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.PetRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Pet createPetForUser(PetDTO petRequest, String username) {
        User petOwner = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pet pet = new Pet(petRequest.getPetId(),petRequest.getPetName(), petRequest.getAge(), petRequest.getWeight(), petOwner);
        return petRepository.save(pet);
    }

    public List<PetDTO> getAllPets() {
        return petRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public List<PetDTO> getAllPetsForUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return petRepository.findAllByPetOwner(user).stream().map(this::convertToDto).collect(Collectors.toList());
    }
    @Transactional
    public List<PetDTO> findPetForUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return petRepository.findAllByPetOwner(user).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    @Transactional
    public Pet updatePetForUser(Long id, Pet pet, String username) {
        Pet existingPet = getPetById(id);
        if (!existingPet.getPetOwner().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to update this pet");
        }
        existingPet.setPetName(pet.getPetName());
        existingPet.setAge(pet.getAge());
        existingPet.setWeight(pet.getWeight());
//        existingPet.setAvatarUrl(pet.getAvatarUrl());
//        existingPet.setAvatarPublicId(pet.getAvatarPublicId());
        return petRepository.save(existingPet);
    }

    @Transactional
    public void deletePetForUser(Long id, String username) {
        Pet existingPet = getPetById(id);
        if (!existingPet.getPetOwner().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to delete this pet");
        }
        petRepository.deleteById(id);
    }

    public PetDTO convertToDto(Pet pet) {
        PetDTO petDTO = new PetDTO();
        petDTO.setPetId(pet.getPetId());
        petDTO.setPetName(pet.getPetName());
        petDTO.setAge(pet.getAge());
        petDTO.setWeight(pet.getWeight());
//        petDTO.setAvatarUrl(pet.getAvatarUrl());
        petDTO.setOwnerFirstName(pet.getPetOwner().getFirstName());
        petDTO.setOwnerLastName(pet.getPetOwner().getLastName());
        petDTO.setOwnerMobileNumber(pet.getPetOwner().getMobileNumber());
        return petDTO;
    }
}
