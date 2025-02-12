//package com.petconnect.backend.services;
//
//import com.petconnect.backend.dto.PetDTO;
//import com.petconnect.backend.entity.Pet;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.exceptions.ResourceNotFoundException;
//import com.petconnect.backend.mapper.PetMapper;
//import com.petconnect.backend.repositories.PetRepository;
//import com.petconnect.backend.repositories.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
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
//    private static final Logger logger = LoggerFactory.getLogger(PetService.class);
//
//    private final PetRepository petRepository;
//    private final UserRepository userRepository;
//    private final PetMapper mapper;
//
//    @Autowired
//    public PetService(PetRepository petRepository, UserRepository userRepository, PetMapper mapper) {
//        this.petRepository = petRepository;
//        this.userRepository = userRepository;
//        this.mapper = mapper;
//    }
//
//    @Transactional
//    public PetDTO createPetForUser(PetDTO petDTO, String username) {
//        try {
//            User petOwner = userRepository.findByEmail(username)
//                    .orElseThrow(() -> {
//                        logger.error("User not found with email: {}", username);
//                        return new ResourceNotFoundException("User not found");
//                    });
//
//            // Validate petRequest fields
//            validatePetRequest(petDTO);
//
//            Pet pet = mapper.toEntity(petDTO);
//            pet.setPetOwner(petOwner);
//
//            Pet savedPet = petRepository.save(pet);
//
//            logger.info("Pet created with ID: {}", savedPet.getPetId());
//            return mapper.toDTO(savedPet);
//        } catch (Exception e) {
//            logger.error("Error creating pet for user: {}", username, e);
//            throw e;
//        }
//    }
//
//    private void validatePetRequest(PetDTO petRequest) {
//        if (petRequest.getPetName() == null || petRequest.getPetName().isEmpty()) {
//            throw new IllegalArgumentException("Pet name is required");
//        }
//        if (petRequest.getAge() == null || petRequest.getAge() < 0) {
//            throw new IllegalArgumentException("Invalid age");
//        }
//        if (petRequest.getWeight() == null || petRequest.getWeight() < 0) {
//            throw new IllegalArgumentException("Invalid weight");
//        }
//    }
//
//    public List<PetDTO> getAllPets() {
//        return petRepository.findAll().stream()
//                .map(mapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public List<PetDTO> getAllPetsForUser(String username) {
//        User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return petRepository.findAllByPetOwner(user).stream()
//                .map(mapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public PetDTO getPetById(Long id) {
//        Pet pet = petRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Pet not found"));
//        return mapper.toDTO(pet);
//    }
//
//    @Transactional
//    public PetDTO updatePetForUser(Long id, PetDTO petDTO, String username) {
//        Pet existingPet = petRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Pet not found"));
//
//        if (!existingPet.getPetOwner().getUsername().equals(username)) {
//            throw new RuntimeException("Unauthorized to update this pet");
//        }
//
//        existingPet.setPetName(petDTO.getPetName());
//        existingPet.setAge(petDTO.getAge());
//        existingPet.setWeight(petDTO.getWeight());
////        existingPet.setAvatarUrl(petDTO.getAvatarUrl());
////        existingPet.setAvatarPublicId(petDTO.getAvatarPublicId());
//
//        Pet updatedPet = petRepository.save(existingPet);
//        return mapper.toDTO(updatedPet);
//    }
//
//    @Transactional
//    public void deletePetForUser(Long id, String username) {
//        Pet existingPet = petRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Pet not found"));
//
//        if (!existingPet.getPetOwner().getUsername().equals(username)) {
//            throw new RuntimeException("Unauthorized to delete this pet");
//        }
//
//        petRepository.deleteById(id);
//    }
//}
