//package com.petconnect.backend.services;
//
//import com.petconnect.backend.dto.PetDTO;
//import com.petconnect.backend.entity.Pet;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.entity.Pet.Gender;
//import com.petconnect.backend.exceptions.ResourceNotFoundException;
//import com.petconnect.backend.mappers.PetMapper;
//import com.petconnect.backend.repositories.PetRepository;
//import com.petconnect.backend.repositories.UserRepository;
//import com.petconnect.backend.validators.PetValidator;
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
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
//    private final PetValidator validator;
//    private final UploadService uploadService;
//
//    @Autowired
//    public PetService(PetRepository petRepository, UserRepository userRepository, PetMapper mapper, PetValidator validator, UploadService uploadService) {
//        this.petRepository = petRepository;
//        this.userRepository = userRepository;
//        this.mapper = mapper;
//        this.validator = validator;
//        this.uploadService = uploadService;
//    }
//
//    @Transactional
//    public PetDTO createPetForUser(@Valid PetDTO petDTO, MultipartFile avatarFile) throws IOException {
//        try {
//            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            String username = userDetails.getUsername();
//
//            User petOwner = userRepository.findByEmail(username)
//                    .orElseThrow(() -> {
//                        logger.error("User not found with email: {}", username);
//                        return new ResourceNotFoundException("User not found");
//                    });
//
//            validator.validate(petDTO);
//
//            // Upload avatar file if present
//            if (avatarFile != null && !avatarFile.isEmpty()) {
//                logger.info("Uploading new profile image for pet");
//                Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile);
//                petDTO.setAvatarUrl((String) uploadResult.get("url"));
//            }
//
//            Pet pet = mapper.toEntity(petDTO);
//            pet.setPetOwner(petOwner);
//            pet.setGender(Gender.valueOf(petDTO.getGender().toUpperCase())); // Convert String to Gender enum
//
//            Pet savedPet = petRepository.save(pet);
//
//            logger.info("Pet created with ID: {}", savedPet.getPetId());
//            return mapper.toDTO(savedPet);
//        } catch (Exception e) {
//            logger.error("Error creating pet for user", e);
//            throw e;
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
//    public List<PetDTO> getAllPetsForUser() {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = userDetails.getUsername();
//
//        User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> {
//                    logger.error("User not found with email: {}", username);
//                    throw new ResourceNotFoundException("User not found");
//                });
//
//        return petRepository.findAllByPetOwner(user).stream()
//                .map(mapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public PetDTO getPetById(Long id) {
//        Pet pet = petRepository.findById(id)
//                .orElseThrow(() -> {
//                    logger.error("Pet not found with ID: {}", id);
//                    throw new ResourceNotFoundException("Pet not found");
//                });
//        return mapper.toDTO(pet);
//    }
//
//    @Transactional
//    public PetDTO updatePetForUser(Long id, @Valid PetDTO petDTO, MultipartFile avatarFile) throws IOException {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = userDetails.getUsername();
//
//        Pet existingPet = petRepository.findById(id)
//                .orElseThrow(() -> {
//                    logger.error("Pet not found with ID: {}", id);
//                    throw new ResourceNotFoundException("Pet not found");
//                });
//
//        if (!existingPet.getPetOwner().getEmail().equals(username)) {
//            logger.error("Unauthorized to update pet with ID: {}", id);
//            throw new RuntimeException("Unauthorized to update this pet");
//        }
//
//        existingPet.setPetName(petDTO.getPetName());
//        existingPet.setAge(petDTO.getAge());
//        existingPet.setWeight(petDTO.getWeight());
//        existingPet.setGender(Gender.valueOf(petDTO.getGender().toUpperCase())); // Convert String to Gender enum
//        existingPet.setBreed(petDTO.getBreed());
//        existingPet.setSpecies(petDTO.getSpecies());
//
//        if (avatarFile != null && !avatarFile.isEmpty()) {
//            logger.info("Uploading new profile image for pet");
//            Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile);
//            existingPet.setAvatarUrl((String) uploadResult.get("url"));
//        } else {
//            existingPet.setAvatarUrl(petDTO.getAvatarUrl());
//        }
//
//        Pet updatedPet = petRepository.save(existingPet);
//        return mapper.toDTO(updatedPet);
//    }
//
//    @Transactional
//    public void deletePetForUser(Long id) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = userDetails.getUsername();
//
//        Pet existingPet = petRepository.findById(id)
//                .orElseThrow(() -> {
//                    logger.error("Pet not found with ID: {}", id);
//                    throw new ResourceNotFoundException("Pet not found");
//                });
//
//        if (!existingPet.getPetOwner().getEmail().equals(username)) {
//            logger.error("Unauthorized to delete pet with ID: {}", id);
//            throw new RuntimeException("Unauthorized to delete this pet");
//        }
//
//        // Check and delete the pet's avatar if it exists
//        if (existingPet.getAvatarUrl() != null) {
//            try {
//                uploadService.deleteImage(existingPet.getAvatarUrl());
//            } catch (IOException e) {
//                logger.error("Error deleting avatar with URL: {}", existingPet.getAvatarUrl(), e);
//            }
//        }
//
//        petRepository.deleteById(id);
//        logger.info("Pet deleted with ID: {}", id);
//    }
//}

package com.petconnect.backend.services;

import com.petconnect.backend.dto.PetDTO;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.entity.Pet.Gender;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.PetMapper;
import com.petconnect.backend.repositories.PetRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.validators.PetValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetMapper mapper;
    private final PetValidator validator;
    private final UploadService uploadService;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository, PetMapper mapper, PetValidator validator, UploadService uploadService) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.validator = validator;
        this.uploadService = uploadService;
    }

    @Transactional
    public PetDTO createPetForUser(@Valid PetDTO petDTO, MultipartFile avatarFile) throws IOException {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();

            User petOwner = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", username);
                        return new ResourceNotFoundException("User not found");
                    });

            validator.validate(petDTO);

            // Upload avatar file if present
            if (avatarFile != null && !avatarFile.isEmpty()) {
                logger.info("Uploading new profile image for pet");
                Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile);
                petDTO.setAvatarUrl((String) uploadResult.get("url"));
            }

            Pet pet = mapper.toEntity(petDTO);
            pet.setPetOwner(petOwner);
            pet.setGender(Gender.valueOf(petDTO.getGender().toUpperCase())); // Convert String to Gender enum

            Pet savedPet = petRepository.save(pet);

            logger.info("Pet created with ID: {}", savedPet.getPetId());
            return mapper.toDTO(savedPet);
        } catch (Exception e) {
            logger.error("Error creating pet for user", e);
            throw e;
        }
    }

    public List<PetDTO> getAllPets() {
        return petRepository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PetDTO> getAllPetsForUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", username);
                    throw new ResourceNotFoundException("User not found");
                });

        return petRepository.findAllByPetOwner(user).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public PetDTO getPetById(Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    throw new ResourceNotFoundException("Pet not found");
                });

        if (!pet.getPetOwner().getEmail().equals(username)) {
            logger.error("Unauthorized access to pet with ID: {}", id);
            throw new RuntimeException("Unauthorized to access this pet");
        }

        return mapper.toDTO(pet);
    }

    @Transactional
    public PetDTO updatePetForUser(Long id, @Valid PetDTO petDTO, MultipartFile avatarFile) throws IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    throw new ResourceNotFoundException("Pet not found");
                });

        if (!existingPet.getPetOwner().getEmail().equals(username)) {
            logger.error("Unauthorized to update pet with ID: {}", id);
            throw new RuntimeException("Unauthorized to update this pet");
        }

        existingPet.setPetName(petDTO.getPetName());
        existingPet.setAge(petDTO.getAge());
        existingPet.setWeight(petDTO.getWeight());
        existingPet.setGender(Gender.valueOf(petDTO.getGender().toUpperCase())); // Convert String to Gender enum
        existingPet.setBreed(petDTO.getBreed());
        existingPet.setSpecies(petDTO.getSpecies());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            logger.info("Uploading new profile image for pet");
            Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile);
            existingPet.setAvatarUrl((String) uploadResult.get("url"));
        } else {
            existingPet.setAvatarUrl(petDTO.getAvatarUrl());
        }

        Pet updatedPet = petRepository.save(existingPet);
        return mapper.toDTO(updatedPet);
    }

    @Transactional
    public void deletePetForUser(Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    throw new ResourceNotFoundException("Pet not found");
                });

        if (!existingPet.getPetOwner().getEmail().equals(username)) {
            logger.error("Unauthorized to delete pet with ID: {}", id);
            throw new RuntimeException("Unauthorized to delete this pet");
        }

        // Check and delete the pet's avatar if it exists
        if (existingPet.getAvatarUrl() != null) {
            try {
                uploadService.deleteImage(existingPet.getAvatarPublicId());
            } catch (IOException e) {
                logger.error("Error deleting avatar with URL: {}", existingPet.getAvatarUrl(), e);
            }
        }

        petRepository.deleteById(id);
        logger.info("Pet deleted with ID: {}", id);
    }
}
