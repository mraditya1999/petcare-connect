package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.pet.PetRequestDTO;
import com.petconnect.backend.dto.pet.PetResponseDTO;
import com.petconnect.backend.exceptions.DuplicateResourceException;
import com.petconnect.backend.exceptions.FileValidationException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UnauthorizedAccessException;
import com.petconnect.backend.services.PetService;
import com.petconnect.backend.utils.FileUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import com.petconnect.backend.validators.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PetController extends BaseController {

    private final PetService petService;
    private final FileValidator fileValidator;

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    public PetController(PetService petService, FileValidator fileValidator) {
        super(logger);
        this.petService = petService;
        this.fileValidator = fileValidator;
    }

    /**
     * Creates a pet for the user.
     *
     * @param userDetails   the authenticated user's details
     * @param petRequestDTO the data transfer object containing pet information
     * @param avatarFiles   the list of uploaded avatar images
     * @return the ResponseEntity containing the ApiResponseDTO with the created pet information
     */
    @Operation(
            summary = "Create a new pet",
            description = "Creates a new pet for the authenticated user. Accepts optional avatar images.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pet created successfully",
                            content = @Content(schema = @Schema(implementation = PetResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or duplicate pet name"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseDTO<PetResponseDTO>> createPetForUser(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Pet creation data") @Valid @ModelAttribute PetRequestDTO petRequestDTO,
            @Parameter(description = "Optional avatar images") @RequestParam(value = "avatarFile", required = false) List<MultipartFile> avatarFiles) {

        logger.info("Received request to create pet for user: {}", userDetails.getUsername());

        try {
            MultipartFile avatarFile = fileValidator.getSingleFile(avatarFiles);
            if (avatarFile != null) {
                fileValidator.validateFile(avatarFile);
            }

            String username = userDetails.getUsername();
            PetResponseDTO createdPet = petService.createPetForUser(petRequestDTO, avatarFile, username);
            logger.info("Pet created for user: {}", username);
            return ResponseEntityUtil.created("Pet created successfully", createdPet);
        } catch (FileValidationException e) {
            logger.error(e.getMessage());
            return ResponseEntityUtil.badRequest(e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntityUtil.notFound("User not found");
        } catch (DuplicateResourceException e) {
            logger.error("Duplicate pet name: {}", e.getMessage());
            return ResponseEntityUtil.badRequest("Duplicate pet name");
        } catch (IOException e) {
            logger.error("IO Error: {}", e.getMessage());
            return ResponseEntityUtil.internalServerError("Error creating pet");
        }
    }

    /**
     * Get all pets for the authenticated user
     *
     * @param userDetails user details of the authenticated user
     * @return ResponseEntity containing a list of pet data
     */
    @Operation(
            summary = "Get all pets for user",
            description = "Fetches all pets for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pets fetched successfully",
                            content = @Content(schema = @Schema(implementation = PetResponseDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PetResponseDTO>>> getAllPetsForUser(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails) {
        List<PetResponseDTO> pets = petService.getAllPetsForUser(userDetails.getUsername());
        return ResponseEntityUtil.ok("Fetched all pets", pets);
    }

    /**
     * Fetches the pet of a user by pet ID.
     *
     * @param id the pet ID
     * @param userDetails the authenticated user details
     * @return the ResponseEntity containing the ApiResponseDTO with the pet information
     */
    @Operation(
            summary = "Get pet by ID",
            description = "Fetch a pet by ID for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pet fetched successfully",
                            content = @Content(schema = @Schema(implementation = PetResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Pet not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PetResponseDTO>> getPetOfUserById(
            @Parameter(description = "Pet ID") @PathVariable Long id,
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails) {
        try {
            PetResponseDTO pet = petService.getPetOfUserById(id, userDetails.getUsername());
            return ResponseEntityUtil.ok("Fetched pet", pet);
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access to pet with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntityUtil.forbidden(e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.error("Pet not found with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntityUtil.notFound(e.getMessage());
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
            @Parameter(description = "Pet ID") @PathVariable Long id,
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Pet update data") @Valid @ModelAttribute PetRequestDTO petRequestDTO,
            @Parameter(description = "Optional avatar images") @RequestParam(value = "avatarFile", required = false) List<MultipartFile> avatarFiles) throws IOException {

        logger.info("Received request to update pet for user: {}", userDetails.getUsername());

        try {
            MultipartFile avatarFile = fileValidator.getSingleFile(avatarFiles);
            if (avatarFile != null) {
                fileValidator.validateFile(avatarFile);
            }

            PetResponseDTO updatedPet = petService.updatePetForUser(id, petRequestDTO, avatarFile, userDetails.getUsername());
            logger.info("Pet updated for user: {}", userDetails.getUsername());
            return ResponseEntityUtil.ok("Pet updated successfully", updatedPet);
        } catch (FileValidationException e) {
            logger.error(e.getMessage());
            return ResponseEntityUtil.badRequest(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access to pet with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntityUtil.forbidden(e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.error("Pet not found with ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntityUtil.notFound(e.getMessage());
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
    public ResponseEntity<ApiResponseDTO<String>> deletePetForUser(
            @Parameter(description = "Pet ID") @PathVariable Long id,
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Received request to delete pet with ID: {} for user: {}", id, userDetails.getUsername());

        petService.deletePetForUser(id, userDetails);
        logger.info("Pet deleted with ID: {}", id);
        return ResponseEntityUtil.ok("Pet deleted successfully", "Pet deleted successfully");
    }
}