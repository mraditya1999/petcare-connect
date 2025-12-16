package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.specialist.SpecialistResponseDTO;
import com.petconnect.backend.dto.specialist.SpecialistUpdateRequestDTO;
import com.petconnect.backend.exceptions.FileValidationException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.services.SpecialistService;
import com.petconnect.backend.utils.ResponseEntityUtil;
import com.petconnect.backend.validators.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private static final Logger logger = LoggerFactory.getLogger(SpecialistController.class);
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
    @Operation(
            summary = "Update current specialist",
            description = "Update the authenticated specialist's profile, including optional profile image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specialist updated successfully",
                            content = @Content(schema = @Schema(implementation = SpecialistResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or file validation error"),
                    @ApiResponse(responseCode = "404", description = "Specialist not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<SpecialistResponseDTO>> updateCurrentSpecialist(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Specialist update information") @Valid @ModelAttribute SpecialistUpdateRequestDTO specialistUpdateRequestDTO,
            @Parameter(description = "Optional profile images") @RequestPart(value = "profileImage", required = false) List<MultipartFile> profileImages,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntityUtil.badRequest("Validation error: " + errorMessage);
        }

        logger.info("Received request to update specialist profile for user: {}", userDetails.getUsername());

        try {
            MultipartFile profileImage = fileValidator.getSingleFile(profileImages);
            if (profileImage != null) {
                fileValidator.validateFile(profileImage);
            }

            SpecialistResponseDTO updatedSpecialist = specialistService.updateSpecialist(specialistUpdateRequestDTO, profileImage, userDetails);
            logger.info("Updated specialist profile for user: {}", userDetails.getUsername());
            return ResponseEntityUtil.ok("Specialist updated successfully", updatedSpecialist);
        } catch (FileValidationException e) {
            logger.error(e.getMessage());
            return ResponseEntityUtil.badRequest(e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found for user: {}", userDetails.getUsername(), e);
            return ResponseEntityUtil.notFound("Specialist not found");
        } catch (IOException e) {
            logger.error("IO Error: {}", e.getMessage(), e);
            return ResponseEntityUtil.internalServerError("Error updating specialist profile");
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
    @Operation(
            summary = "Get all specialists",
            description = "Fetch all specialists with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specialists fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid sort direction or pagination parameters")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<SpecialistResponseDTO>>> getAllSpecialists(
            @Parameter(description = "Page number (starting from 0)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "userId") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            // Create pageable object with given page, size, sortBy, and sortDir
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            // Fetch the specialists with pagination and sorting
            Page<SpecialistResponseDTO> specialists = specialistService.getAllSpecialists(pageable);

            logger.info("Fetched all specialists with pagination and sorting");
            return ResponseEntityUtil.page(specialists, "Fetched all specialists");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid sort direction: {}", e.getMessage());
            return ResponseEntityUtil.badRequest("Invalid sort direction");
        }
    }

    /**
     * Fetches a specialist by ID.
     *
     * @param id The ID of the specialist to fetch
     * @return ResponseEntity with ApiResponse containing the specialist details
     */

    @Operation(
            summary = "Get specialist by ID",
            description = "Fetch a specialist's details by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specialist fetched successfully",
                            content = @Content(schema = @Schema(implementation = SpecialistResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Specialist not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SpecialistResponseDTO>> getSpecialistById(
            @Parameter(description = "ID of the specialist to fetch") @PathVariable Long id) {
        try {
            SpecialistResponseDTO specialist = specialistService.getSpecialistById(id);
            logger.info("Fetched specialist with ID: {}", id);
            return ResponseEntityUtil.ok("Fetched specialist", specialist);
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found with ID: {}", id, e);
            return ResponseEntityUtil.notFound("Specialist not found");
        }
    }
}
