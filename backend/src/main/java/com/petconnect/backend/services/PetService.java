package com.petconnect.backend.services;

import com.petconnect.backend.dto.PetDTO;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.entity.Pet.Gender;
import com.petconnect.backend.exceptions.ImageDeletionException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.PetMapper;
import com.petconnect.backend.repositories.PetRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.validators.PetValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final PetMapper petMapper;
    private final PetValidator validator;
    private final UploadService uploadService;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository, PetMapper petMapper, PetValidator validator, UploadService uploadService) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.petMapper = petMapper;
        this.validator = validator;
        this.uploadService = uploadService;
    }

    @Transactional
    public PetDTO createPetForUser(@Valid PetDTO petDTO, MultipartFile avatarFile, @AuthenticationPrincipal String username) throws IOException {
        try {

            User petOwner = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", username);
                        return new ResourceNotFoundException("User not found");
                    });

            validator.validate(petDTO);

            // Upload avatar file if present
            if (avatarFile != null && !avatarFile.isEmpty()) {
                logger.info("Uploading new profile image for pet");
                Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile, UploadService.ProfileType.PET);
                petDTO.setAvatarUrl((String) uploadResult.get("url"));
                petDTO.setAvatarPublicId((String) uploadResult.get("public_id"));
            }

            Pet pet = petMapper.toEntity(petDTO);
            pet.setPetOwner(petOwner);
            pet.setGender(Gender.valueOf(petDTO.getGender().toUpperCase())); // Convert String to Gender enum

            Pet savedPet = petRepository.save(pet);

            logger.info("Pet created with ID: {}", savedPet.getPetId());
            return petMapper.toDTO(savedPet);
        } catch (Exception e) {
            logger.error("Error creating pet for user", e);
            throw e;
        }
    }

    @Transactional
    public List<PetDTO> getAllPetsForUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", username);
                    return new ResourceNotFoundException("User not found");
                });

        return petRepository.findAllByPetOwner(user).stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PetDTO getPetOfUserById(Long id) {
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

        return petMapper.toDTO(pet);
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

        if (petDTO.getPetName() != null) existingPet.setPetName(petDTO.getPetName());
        if (petDTO.getAge() != null) existingPet.setAge(petDTO.getAge());
        if (petDTO.getWeight() != null) existingPet.setWeight(petDTO.getWeight());
        if (petDTO.getGender() != null) existingPet.setGender(Gender.valueOf(petDTO.getGender().toUpperCase()));
        if (petDTO.getBreed() != null) existingPet.setBreed(petDTO.getBreed());
        if (petDTO.getSpecies() != null) existingPet.setSpecies(petDTO.getSpecies());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Delete old image first
            if (existingPet.getAvatarPublicId() != null && !existingPet.getAvatarPublicId().isEmpty()) {
                logger.info("Deleting old profile image for pet with ID: {}", existingPet.getPetId());
                try {
                    uploadService.deleteImage(existingPet.getAvatarPublicId());
                } catch (IOException e) {
                    logger.error("Error deleting old avatar with ID: {}", existingPet.getAvatarPublicId(), e);
                    // Handle the error (e.g., log it or throw an exception)
                    throw new ImageDeletionException("Error deleting old avatar image", e);
                }
            }

            // Upload new image
            logger.info("Uploading new profile image for pet");
            Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile, UploadService.ProfileType.PET);
            existingPet.setAvatarUrl((String) uploadResult.get("url"));
            existingPet.setAvatarPublicId((String) uploadResult.get("public_id"));
        } else {
            if (petDTO.getAvatarUrl() != null) {
                existingPet.setAvatarUrl(petDTO.getAvatarUrl());
            }
        }

        Pet updatedPet = petRepository.save(existingPet);
        return petMapper.toDTO(updatedPet);
    }

    @Transactional
    public void deletePetForUser(Long id, UserDetails userDetails) {
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
        if (existingPet.getAvatarPublicId() != null && !existingPet.getAvatarPublicId().isEmpty()) {
            try {
                uploadService.deleteImage(existingPet.getAvatarPublicId());
            } catch (IOException e) {
                logger.error("Error deleting avatar with ID: {}", existingPet.getAvatarPublicId(), e);
                throw new ImageDeletionException("Error deleting avatar image", e);
            }
        }

        petRepository.deleteById(id);
        logger.info("Pet deleted with ID: {}", id);
    }



//    ADMIN SERVICES
public Page<PetDTO> getAllPets(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Pet> petPage = petRepository.findAll(pageable);
    return petPage.map(petMapper::toDTO);
}

    public PetDTO getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    throw new ResourceNotFoundException("Pet not found");
                });

        return petMapper.toDTO(pet);
    }

    @Transactional
    public PetDTO updatePetById(Long id, @Valid PetDTO petDTO, MultipartFile avatarFile) throws IOException {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    throw new ResourceNotFoundException("Pet not found");
                });

        if (petDTO.getPetName() != null) existingPet.setPetName(petDTO.getPetName());
        if (petDTO.getAge() != null) existingPet.setAge(petDTO.getAge());
        if (petDTO.getWeight() != null) existingPet.setWeight(petDTO.getWeight());
        if (petDTO.getGender() != null) existingPet.setGender(Gender.valueOf(petDTO.getGender().toUpperCase()));
        if (petDTO.getBreed() != null) existingPet.setBreed(petDTO.getBreed());
        if (petDTO.getSpecies() != null) existingPet.setSpecies(petDTO.getSpecies());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Delete old image first
            if (existingPet.getAvatarPublicId() != null && !existingPet.getAvatarPublicId().isEmpty()) {
                logger.info("Deleting old profile image for pet with ID: {}", existingPet.getPetId());
                try {
                    uploadService.deleteImage(existingPet.getAvatarPublicId());
                } catch (IOException e) {
                    logger.error("Error deleting old avatar with ID: {}", existingPet.getAvatarPublicId(), e);
                    throw new ImageDeletionException("Error deleting old avatar image", e);
                }
            }

            // Upload new image
            logger.info("Uploading new profile image for pet");
            Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile, UploadService.ProfileType.PET);
            existingPet.setAvatarUrl((String) uploadResult.get("url"));
            existingPet.setAvatarPublicId((String) uploadResult.get("public_id"));
        } else {
            if (petDTO.getAvatarUrl() != null) {
                existingPet.setAvatarUrl(petDTO.getAvatarUrl());
            }
        }

        Pet updatedPet = petRepository.save(existingPet);
        return petMapper.toDTO(updatedPet);
    }


    @Transactional
    public void deletePetById(Long id) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    throw new ResourceNotFoundException("Pet not found");
                });

        // Remove references from User table
        User petOwner = existingPet.getPetOwner();
        petOwner.getPets().remove(existingPet);
        userRepository.save(petOwner);

        // Check and delete the pet's avatar if it exists
        if (existingPet.getAvatarPublicId() != null && !existingPet.getAvatarPublicId().isEmpty()) {
            try {
                uploadService.deleteImage(existingPet.getAvatarPublicId());
            } catch (IOException e) {
                logger.error("Error deleting avatar with ID: {}", existingPet.getAvatarPublicId(), e);
                throw new ImageDeletionException("Error deleting avatar image", e);
            }
        }

        petRepository.deleteById(id);
        logger.info("Pet deleted with ID: {}", id);
    }


}
