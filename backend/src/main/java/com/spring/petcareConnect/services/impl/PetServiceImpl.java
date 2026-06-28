package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.pet.request.PetRequestDto;
import com.spring.petcareConnect.dtos.pet.response.PetListResponseDto;
import com.spring.petcareConnect.dtos.pet.response.PetResponseDto;
import com.spring.petcareConnect.entities.Breed;
import com.spring.petcareConnect.entities.Pet;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.Gender;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.exceptions.DuplicateResourceException;
import com.spring.petcareConnect.exceptions.ResourceNotFoundException;
import com.spring.petcareConnect.helpers.PetProfileImageHandler;
import com.spring.petcareConnect.repositories.jpa.BreedRepository;
import com.spring.petcareConnect.repositories.jpa.PetRepository;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.services.PetService;
import com.spring.petcareConnect.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetServiceImpl.class);

    private final UserRepository userRepository;
    private final BreedRepository breedRepository;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final PetProfileImageHandler petProfileImageHandler;


    public PetServiceImpl(UserRepository userRepository, BreedRepository breedRepository, PetRepository petRepository, ModelMapper modelMapper,  PetProfileImageHandler petProfileImageHandler) {
        this.userRepository = userRepository;
        this.breedRepository = breedRepository;
        this.petRepository = petRepository;
        this.modelMapper = modelMapper;
        this.petProfileImageHandler = petProfileImageHandler;
    }

    @Override
    @Transactional
    public PetResponseDto createPetForUser(PetRequestDto petRequestDTO, MultipartFile profileImage) {
        logger.info("Creating pet for user with email lookup...");
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during pet creation");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        logger.debug("Checking for duplicate pet name: {}", petRequestDTO.getPetName());
        if (petRepository.existsByPetOwnerAndPetName(user, petRequestDTO.getPetName())) {
            logger.warn("Duplicate pet name '{}' for user {}", petRequestDTO.getPetName(), email);
            throw new DuplicateResourceException("Pet", "name", petRequestDTO.getPetName());
        }

        Breed breed = breedRepository.findById(petRequestDTO.getBreed())
                .orElseThrow(() -> {
                    logger.error("Breed not found with id {}", petRequestDTO.getBreed());
                    return ResourceNotFoundException.byId("Breed", petRequestDTO.getBreed());
                });
        String petName = petRequestDTO.getPetName();
        petRequestDTO.setPetName(petName);
        Pet pet = modelMapper.map(petRequestDTO, Pet.class);
        pet.setPetOwner(user);
        pet.setBreed(breed);

        if (profileImage != null && !profileImage.isEmpty()) {
            logger.info("Uploading profile image for new pet '{}'", pet.getPetName());
            petProfileImageHandler.create(pet, profileImage);
        }

        Pet savedPet = petRepository.save(pet);
        logger.info("Successfully created pet '{}' with id {}", savedPet.getPetName(), savedPet.getPetId());
        return modelMapper.map(savedPet, PetResponseDto.class);
    }

    @Override
    @Transactional
    public PetResponseDto updatePetForUser(Long petId, PetRequestDto petRequestDTO, MultipartFile profileImage) {
        logger.info("Updating pet with id {}", petId);
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during pet update");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Pet existingPet = petRepository.findById(petId).orElseThrow(() -> {
            logger.error("Pet not found with id {}", petId);
            return new ResourceNotFoundException("Pet", "id", petId);
        });

        String newName = petRequestDTO.getPetName();
        if (!existingPet.getPetName().equalsIgnoreCase(newName)) {
            logger.debug("Updating pet name from '{}' to '{}'", existingPet.getPetName(), newName);
            if (petRepository.existsByPetOwnerAndPetName(user, newName)) {
                logger.warn("Duplicate pet name '{}' for user {}", newName, email);
                throw new DuplicateResourceException("Pet", "name", newName);
            }
            existingPet.setPetName(newName);
        }

        existingPet.setAge(petRequestDTO.getAge());
        existingPet.setWeight(petRequestDTO.getWeight());
        existingPet.setGender(petRequestDTO.getGender());

        Breed breed = breedRepository.findById(petRequestDTO.getBreed())
                .orElseThrow(() -> {
                    logger.error("Breed not found with id {}", petRequestDTO.getBreed());
                    return ResourceNotFoundException.byId("Breed", petRequestDTO.getBreed());
                });
        existingPet.setBreed(breed);

        if (profileImage != null && !profileImage.isEmpty()) {
            logger.info("Updating profile image for pet '{}'", existingPet.getPetName());
            if (existingPet.getAvatarPublicId() != null && !existingPet.getAvatarPublicId().isBlank()) {
                petProfileImageHandler.replace(existingPet, profileImage);
            } else {
                petProfileImageHandler.create(existingPet, profileImage);
            }
        }

        Pet updatedPet = petRepository.save(existingPet);
        logger.info("Successfully updated pet '{}' with id {}", updatedPet.getPetName(), updatedPet.getPetId());
        return modelMapper.map(updatedPet, PetResponseDto.class);
    }

    @Override
    public PetListResponseDto getAllPetsForUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        logger.info("Fetching all pets for user with pagination page={} size={} sortBy={} sortOrder={}",
                pageNumber, pageSize, sortBy, sortOrder);
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during pet list retrieval");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Pet> petPage = petRepository.findAllByPetOwner(user, pageable);
        logger.debug("Found {} pets for user {}", petPage.getTotalElements(), email);
        return buildResponse(petPage);
    }

    @Override
    public PetResponseDto getPetOfUserById(Long petId) {
        logger.info("Fetching pet with id {}", petId);
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during pet fetch");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Pet pet = petRepository.findByPetIdAndPetOwner(petId, user).orElseThrow(() -> {
            logger.error("Pet not found with id {}", petId);
            return new ResourceNotFoundException("Pet", "id", petId);
        });
        logger.info("Successfully fetched pet '{}' with id {}", pet.getPetName(), pet.getPetId());
        return convertToDto(pet);
    }

    @Override
    public void deletePetForUser(Long petId) {
        logger.info("Deleting pet with id {}", petId);
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during pet deletion");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Pet pet = petRepository.findByPetIdAndPetOwner(petId, user).orElseThrow(() -> {
            logger.error("Pet not found with id {}", petId);
            return new ResourceNotFoundException("Pet", "id", petId);
        });

        petProfileImageHandler.delete(pet);
        petRepository.delete(pet);
        logger.info("Successfully deleted pet '{}' with id {}", pet.getPetName(), pet.getPetId());
    }

    private PetListResponseDto buildResponse(Page<Pet> petPage) {
        List<PetResponseDto> pets = petPage.getContent().stream().map(this::convertToDto).toList();
        return new PetListResponseDto(pets, petPage.getNumber(), petPage.getSize(), petPage.getTotalElements(), petPage.getTotalPages(), petPage.isLast());
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));
    }

    private Pageable buildPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }

    private PetResponseDto convertToDto(Pet pet) {
        return modelMapper.map(pet, PetResponseDto.class);
    }
}
