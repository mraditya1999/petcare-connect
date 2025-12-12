package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.pet.PetRequestDTO;
import com.petconnect.backend.dto.pet.PetResponseDTO;
import com.petconnect.backend.exceptions.DuplicatePetNameException;
import com.petconnect.backend.exceptions.FileValidationException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UnauthorizedAccessException;
import com.petconnect.backend.services.PetService;
import com.petconnect.backend.utils.FileUtils;
import com.petconnect.backend.validators.FileValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    private final PetService petService;
    private final FileUtils fileUtils;
    private final FileValidator fileValidator;

    @Autowired
    public PetController(PetService petService, FileUtils fileUtils, FileValidator fileValidator) {
        this.petService = petService;
        this.fileUtils = fileUtils;
        this.fileValidator = fileValidator;
    }

    /**
     * Creates a pet for the user.
     *
     * @param userDetails   the authenticated user's details
     * @param petRequestDTO the data transfer object containing pet information
     * @param avatarFiles   the list of uploaded avatar images
     * @return the ResponseEntity containing the ApiResponseDTO with the created pet information
     * @throws IOException if an I/O error occurs
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseDTO<PetResponseDTO>> createPetForUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PetRequestDTO petRequestDTO,
            @RequestParam(value = "avatarFile", required = false) List<MultipartFile> avatarFiles
    ) throws IOException {

        logger.info("Received request to create pet for user: {}", userDetails.getUsername());

        try {
            MultipartFile avatarFile = fileValidator.getSingleFile(avatarFiles);
            if (avatarFile != null) {
                fileValidator.validateFile(avatarFile);
            }

            String username = userDetails.getUsername();
            PetResponseDTO createdPet = petService.createPetForUser(petRequestDTO, avatarFile, username);
            logger.info("Pet created for user: {}", username);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>("Pet created successfully", createdPet));
        } catch (FileValidationException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User not found", null));
        } catch (DuplicatePetNameException e) {
            logger.error("Duplicate pet name: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>("Duplicate pet name", null));
        } catch (IOException e) {
            logger.error("IO Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error creating pet", null));
        }
    }

    /**
     * Get all pets for the authenticated user
     *
     * @param userDetails user details of the authenticated user
     * @return ResponseEntity containing a list of pet data
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PetResponseDTO>>> getAllPetsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        List<PetResponseDTO> pets = petService.getAllPetsForUser(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponseDTO<>("Fetched all pets", pets));
    }

    /**
     * Fetches the pet of a user by pet ID.
     *
     * @param id the pet ID
     * @param userDetails the authenticated user details
     * @return the ResponseEntity containing the ApiResponseDTO with the pet information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PetResponseDTO>> getPetOfUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PetResponseDTO pet = petService.getPetOfUserById(id, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched pet", pet));
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access to pet with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO<>(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            logger.error("Pet not found with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>(e.getMessage(), null));
        }
    }

    /**
     * Updates the pet of a user by pet ID.
     *
     * @param id the pet ID
     * @param userDetails the authenticated user details
     * @param petRequestDTO the pet request data transfer object
     * @param avatarFiles the list of uploaded avatar images
     * @return the ResponseEntity containing the ApiResponseDTO with the updated pet information
     */
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponseDTO<PetResponseDTO>> updatePetForUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute PetRequestDTO petRequestDTO,
            @RequestParam(value = "avatarFile", required = false) List<MultipartFile> avatarFiles) throws IOException {

        logger.info("Received request to update pet for user: {}", userDetails.getUsername());

        try {
            MultipartFile avatarFile = fileValidator.getSingleFile(avatarFiles);
            if (avatarFile != null) {
                fileValidator.validateFile(avatarFile);
            }

            PetResponseDTO updatedPet = petService.updatePetForUser(id, petRequestDTO, avatarFile, userDetails.getUsername());
            logger.info("Pet updated for user: {}", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponseDTO<>("Pet updated successfully", updatedPet));
        } catch (FileValidationException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(e.getMessage(), null));
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access to pet with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO<>(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            logger.error("Pet not found with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>(e.getMessage(), null));
        }
    }

    /**
     * Deletes the pet of a user by pet ID.
     *
     * @param id the pet ID
     * @param userDetails the authenticated user details
     * @return the ResponseEntity containing the ApiResponseDTO with the deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deletePetForUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Received request to delete pet with ID: {} for user: {}", id, userDetails.getUsername());

        petService.deletePetForUser(id, userDetails);
        logger.info("Pet deleted with ID: {}", id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Pet deleted successfully"));
    }
}