package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.specialist.SpecialistResponseDTO;
import com.petconnect.backend.dto.specialist.SpecialistUpdateRequestDTO;
import com.petconnect.backend.exceptions.FileValidationException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.services.SpecialistService;
import com.petconnect.backend.validators.FileValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private static final Logger log = LoggerFactory.getLogger(SpecialistController.class);

    private final SpecialistService specialistService;
    private final SpecialistMapper specialistMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final FileValidator fileValidator;

    @Autowired
    public SpecialistController(SpecialistService specialistService, SpecialistMapper specialistMapper, FileValidator fileValidator) {
        this.specialistService = specialistService;
        this.specialistMapper = specialistMapper;
        this.fileValidator = fileValidator;
    }

    /**
     * Update the current specialist's profile.
     * Consumes multipart/form-data for profile image.
     *
     * @param userDetails Authenticated user's details
     * @param specialistUpdateRequestDTO DTO containing specialist information
     * @param profileImages List of profile image files
     * @param bindingResult BindingResult for validation errors
     * @return ResponseEntity with ApiResponse containing the updated specialist
     */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<SpecialistResponseDTO>> updateCurrentSpecialist(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute SpecialistUpdateRequestDTO specialistUpdateRequestDTO,
            @RequestPart(value = "profileImage", required = false) List<MultipartFile> profileImages,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>("Validation error: " + errorMessage, null));
        }

        logger.info("Received request to update specialist profile for user: {}", userDetails.getUsername());

        try {
            MultipartFile profileImage = fileValidator.getSingleFile(profileImages);
            if (profileImage != null) {
                fileValidator.validateFile(profileImage);
            }

            SpecialistResponseDTO updatedSpecialist = specialistService.updateSpecialist(specialistUpdateRequestDTO, profileImage, userDetails);
            SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(updatedSpecialist);
            logger.info("Updated specialist profile for user: {}", userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponseDTO<>("Specialist updated successfully", specialistResponseDTO));
        } catch (FileValidationException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("Specialist not found", null));
        } catch (IOException e) {
            logger.error("IO Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error updating specialist profile", null));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error updating specialist profile", null));
        }
    }

    /**
     * Fetches all specialists with pagination and sorting.
     *
     * @param page   The page number to retrieve (default is 0)
     * @param size   The number of records per page (default is 10)
     * @param sortBy The field to sort by (default is userId)
     * @param sortDir The direction of sorting: "asc" for ascending, "desc" for descending (default is asc)
     * @return ResponseEntity with ApiResponse containing a paginated list of specialists
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<SpecialistResponseDTO>>> getAllSpecialists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            // Create pageable object with given page, size, sortBy, and sortDir
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            // Fetch the specialists with pagination and sorting
            Page<SpecialistResponseDTO> specialists = specialistService.getAllSpecialists(pageable);

            logger.info("Fetched all specialists with pagination and sorting");
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched all specialists", specialists));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid sort direction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>("Invalid sort direction", null));
        } catch (Exception e) {
            logger.error("Error fetching specialists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error fetching specialists", null));
        }
    }

    /**
     * Fetches a specialist by ID.
     *
     * @param id The ID of the specialist to fetch
     * @return ResponseEntity with ApiResponse containing the specialist details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SpecialistResponseDTO>> getSpecialistById(@PathVariable Long id) {
        try {
            SpecialistResponseDTO specialist = specialistService.getSpecialistById(id);
            logger.info("Fetched specialist with ID: {}", id);
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched specialist", specialist));
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("Specialist not found", null));
        } catch (Exception e) {
            logger.error("Error fetching specialist with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error fetching specialist", null));
        }
    }
}
