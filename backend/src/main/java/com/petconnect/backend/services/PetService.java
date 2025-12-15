package com.petconnect.backend.services;

import com.petconnect.backend.dto.pet.PetRequestDTO;
import com.petconnect.backend.dto.pet.PetResponseDTO;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.DuplicateResourceException;
import com.petconnect.backend.exceptions.ImageDeletionException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UnauthorizedAccessException;
import com.petconnect.backend.mappers.PetMapper;
import com.petconnect.backend.repositories.PetRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.validators.PetValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UploadService uploadService;
    private final PetValidator petValidator;

    @Autowired
    public PetService(PetRepository petRepository, UserRepository userRepository, PetMapper petMapper, UploadService uploadService, PetValidator petValidator) {
        if (petRepository == null) {
            throw new IllegalArgumentException("PetRepository cannot be null");
        }
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }
        if (petMapper == null) {
            throw new IllegalArgumentException("PetMapper cannot be null");
        }
        if (uploadService == null) {
            throw new IllegalArgumentException("UploadService cannot be null");
        }
        if (petValidator == null) {
            throw new IllegalArgumentException("PetValidator cannot be null");
        }
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.petMapper = petMapper;
        this.uploadService = uploadService;
        this.petValidator = petValidator;
    }

    /**
     * Create a new pet for the authenticated user.
     *
     * @param petRequestDTO pet data for the request
     * @param avatarFile optional avatar file for the pet
     * @param username username of the authenticated user
     * @return PetResponseDTO containing the created pet data
     * @throws IOException if an I/O error occurs while processing the avatar file
     */
    @Transactional
    public PetResponseDTO createPetForUser(@Valid PetRequestDTO petRequestDTO, MultipartFile avatarFile, String username) throws IOException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (petRequestDTO == null) {
            throw new IllegalArgumentException("PetRequestDTO cannot be null");
        }
        
        logger.info("Received request to create a pet for user: {}", username);

        try {
            User petOwner = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", username);
                        return new ResourceNotFoundException("User not found");
                    });

            petValidator.validate(petRequestDTO);

            if (petRequestDTO.getPetName() != null && 
                petRepository.existsByPetOwnerAndPetName(petOwner, petRequestDTO.getPetName())) {
                logger.error("Duplicate pet name: {} for user: {}", petRequestDTO.getPetName(), username);
                throw new DuplicateResourceException("A pet with the same name already exists for this user");
            }

            Pet pet = petMapper.toEntity(petRequestDTO);
            pet.setPetOwner(petOwner);
            if (petRequestDTO.getGender() != null && !petRequestDTO.getGender().isBlank()) {
                try {
                    pet.setGender(Pet.Gender.valueOf(petRequestDTO.getGender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid gender value: {}", petRequestDTO.getGender());
                    throw new IllegalArgumentException("Invalid gender value: " + petRequestDTO.getGender());
                }
            }

            Pet savedPet = petRepository.save(pet);
            logger.info("Pet created with ID: {}", savedPet.getPetId());

            if (avatarFile != null && !avatarFile.isEmpty()) {
                logger.info("Uploading new profile image for pet");
                Map<String, Object> uploadResult = uploadService.uploadImage(avatarFile, UploadService.ProfileType.PET);
                savedPet.setAvatarUrl((String) uploadResult.get("url"));
                savedPet.setAvatarPublicId((String) uploadResult.get("public_id"));
                savedPet = petRepository.save(savedPet); // Update the pet with avatar information
                logger.info("Updated pet with avatar image ID: {}", savedPet.getPetId());
            }

            return petMapper.toDTO(savedPet);
        } catch (ResourceNotFoundException | DuplicateResourceException | IllegalArgumentException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Error uploading avatar for pet creation for user: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error creating pet for user: {}", username, e);
            throw new RuntimeException("Failed to create pet", e);
        }
    }

    /**
     * Get all pets for the authenticated user
     *
     * @param username username of the authenticated user
     * @return List of PetResponseDTO containing the pet data
     */
    @Transactional
    public List<PetResponseDTO> getAllPetsForUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", username);
                    return new ResourceNotFoundException("User not found");
                });

        return petRepository.findAllByPetOwner(user).stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific pet by ID for the authenticated user
     *
     * @param id pet ID
     * @param username username of the authenticated user
     * @return PetResponseDTO containing the pet data
     */
    public PetResponseDTO getPetOfUserById(Long id, String username) {
        if (id == null) {
            throw new IllegalArgumentException("Pet ID cannot be null");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        
        try {
            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Pet not found with ID: {}", id);
                        return new ResourceNotFoundException("Pet not found");
                    });

            if (pet.getPetOwner() == null || !username.equals(pet.getPetOwner().getEmail())) {
                logger.error("Unauthorized access to pet with ID: {} by user: {}", id, username);
                throw new UnauthorizedAccessException("Unauthorized to access this pet");
            }

            return petMapper.toDTO(pet);
        } catch (ResourceNotFoundException | UnauthorizedAccessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving pet {} for user {}", id, username, e);
            throw new RuntimeException("Failed to retrieve pet", e);
        }
    }


    /**
     * Update a pet by ID for the authenticated user
     *
     * @param id pet ID
     * @param petRequestDTO updated pet data for the request
     * @param avatarFile optional updated avatar file for the pet
     * @param username username of the authenticated user
     * @return PetResponseDTO containing the updated pet data
     * @throws IOException if an I/O error occurs while processing the avatar file
     */
    @Transactional
    public PetResponseDTO updatePetForUser(Long id, @Valid PetRequestDTO petRequestDTO, MultipartFile avatarFile, String username) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("Pet ID cannot be null");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (petRequestDTO == null) {
            throw new IllegalArgumentException("PetRequestDTO cannot be null");
        }
        
        try {
            Pet existingPet = petRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Pet not found with ID: {}", id);
                        return new ResourceNotFoundException("Pet not found");
                    });

            if (existingPet.getPetOwner() == null || !username.equals(existingPet.getPetOwner().getEmail())) {
                logger.error("Unauthorized to update pet with ID: {} by user: {}", id, username);
                throw new UnauthorizedAccessException("Unauthorized to update this pet");
            }

            petValidator.validate(petRequestDTO);

            // Update pet details
            if (petRequestDTO.getPetName() != null && !petRequestDTO.getPetName().isBlank()) {
                existingPet.setPetName(petRequestDTO.getPetName().trim());
            }
            if (petRequestDTO.getAge() > 0) {
                existingPet.setAge(petRequestDTO.getAge());
            }
            if (petRequestDTO.getWeight() != null) {
                existingPet.setWeight(petRequestDTO.getWeight());
            }
            if (petRequestDTO.getGender() != null && !petRequestDTO.getGender().isBlank()) {
                try {
                    existingPet.setGender(Pet.Gender.valueOf(petRequestDTO.getGender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid gender value: {}", petRequestDTO.getGender());
                    throw new IllegalArgumentException("Invalid gender value: " + petRequestDTO.getGender());
                }
            }
            if (petRequestDTO.getBreed() != null) {
                existingPet.setBreed(petRequestDTO.getBreed().trim());
            }
            if (petRequestDTO.getSpecies() != null) {
                existingPet.setSpecies(petRequestDTO.getSpecies().trim());
            }

            // Upload avatar file if present
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
                if (petRequestDTO.getAvatarUrl() != null) {
                    existingPet.setAvatarUrl(petRequestDTO.getAvatarUrl());
                }
            }

            Pet updatedPet = petRepository.save(existingPet);
            logger.info("Pet {} updated by user {}", id, username);
            return petMapper.toDTO(updatedPet);
        } catch (ResourceNotFoundException | UnauthorizedAccessException | ImageDeletionException | IllegalArgumentException e) {
            throw e;
        } catch (IOException e) {
            logger.error("Error uploading avatar for pet update for user: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating pet {} for user {}", id, username, e);
            throw new RuntimeException("Failed to update pet", e);
        }
    }

    /**
     * Delete a pet by ID for the authenticated user
     *
     * @param id pet ID
     * @param userDetails user details of the authenticated user
     */
    @Transactional
    public void deletePetForUser(Long id, UserDetails userDetails) {
        String username = userDetails.getUsername();

        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    return new ResourceNotFoundException("Pet not found");
                });

        if (!existingPet.getPetOwner().getEmail().equals(username)) {
            logger.error("Unauthorized to delete pet with ID: {}", id);
            throw new UnauthorizedAccessException("Unauthorized to delete this pet");
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


    /**
     * Get all pets with pagination and sorting.
     *
     * @param pageable the pagination and sorting information
     * @return Page of PetResponseDTO containing the pet data
     */
    public Page<PetResponseDTO> getAllPets(Pageable pageable) {
        Page<Pet> petPage = petRepository.findAll(pageable);
        return petPage.map(petMapper::toDTO);
    }

    /**
     * Get a specific pet by ID.
     *
     * @param id pet ID
     * @return PetResponseDTO containing the pet data
     */
    public PetResponseDTO getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    return new ResourceNotFoundException("Pet not found");
                });

        return petMapper.toDTO(pet);
    }

    /**
     * Update a pet by ID.
     *
     * @param id pet ID
     * @param petRequestDTO updated pet data for the request
     * @param avatarFile optional updated avatar file for the pet
     * @return PetResponseDTO containing the updated pet data
     * @throws IOException if an I/O error occurs while processing the avatar file
     */
    @Transactional
    public PetResponseDTO updatePetById(Long id, @Valid PetRequestDTO petRequestDTO, MultipartFile avatarFile) throws IOException {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    return new ResourceNotFoundException("Pet not found");
                });

        if (petRequestDTO.getPetName() != null) existingPet.setPetName(petRequestDTO.getPetName());
        if (petRequestDTO.getAge() != 0) existingPet.setAge(petRequestDTO.getAge());
        if (petRequestDTO.getWeight() != null) existingPet.setWeight(petRequestDTO.getWeight());
        if (petRequestDTO.getGender() != null) existingPet.setGender(Pet.Gender.valueOf(petRequestDTO.getGender().toUpperCase()));
        if (petRequestDTO.getBreed() != null) existingPet.setBreed(petRequestDTO.getBreed());
        if (petRequestDTO.getSpecies() != null) existingPet.setSpecies(petRequestDTO.getSpecies());

        // Upload avatar file if present
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
            if (petRequestDTO.getAvatarUrl() != null) {
                existingPet.setAvatarUrl(petRequestDTO.getAvatarUrl());
            }
        }

        Pet updatedPet = petRepository.save(existingPet);
        return petMapper.toDTO(updatedPet);
    }

    /**
     * Delete a pet by ID.
     *
     * @param id pet ID
     */
    @Transactional
    public void deletePetById(Long id) {
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", id);
                    return new ResourceNotFoundException("Pet not found");
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
