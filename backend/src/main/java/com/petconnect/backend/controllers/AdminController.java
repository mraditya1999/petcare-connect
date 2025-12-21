package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.dto.forum.CommentDTO;
import com.petconnect.backend.dto.forum.ForumDTO;
import com.petconnect.backend.dto.forum.LikeDTO;
import com.petconnect.backend.dto.forum.UpdateForumDTO;
import com.petconnect.backend.dto.pet.PetRequestDTO;
import com.petconnect.backend.dto.pet.PetResponseDTO;
import com.petconnect.backend.dto.specialist.*;
import com.petconnect.backend.dto.user.UserDTO;
import com.petconnect.backend.dto.user.UserUpdateDTO;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.Like;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.exceptions.*;
import com.petconnect.backend.exceptions.IllegalArgumentException;
import com.petconnect.backend.mappers.CommentMapper;
import com.petconnect.backend.mappers.LikeMapper;
import com.petconnect.backend.services.*;
import com.petconnect.backend.utils.FileUtils;
import com.petconnect.backend.utils.ResponseEntityUtil;
import com.petconnect.backend.validators.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Controller", description = "Admin operations for Users, Pets, Specialists, Forum, Comments, and Likes")
public class AdminController {

    private final UserService userService;
    private final PetService petService;
    private final SpecialistService specialistService;
    private final ForumService forumService;
    private final LikeService likeService;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final FileValidator fileValidator;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

//    ############################################################# USER #########################################################
    /**
     * Get all users with pagination and sorting.
     *
     * @param page    the page number to retrieve (default is 0)
     * @param size    the number of items per page (default is 10)
     * @param sortBy  the field to sort by (default is "userId")
     * @param sortDir the sort direction (default is "asc")
     * @return a response entity containing a page of users
     */
    @Operation(summary = "Get all users", description = "Fetch all users with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
            })
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDTO<Page<UserDTO>>> getAllUsers(
            @Parameter(description = "Page number (starting from 0)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "userId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<UserDTO> users = userService.getAllUsers(pageable);
        logger.info("Fetched all users with pagination and sorting");
        return ResponseEntityUtil.page(users, "Fetched all users");
    }

    /**
     * Get user by ID.
     *
     * @param userId the ID of the user to retrieve
     * @return a response entity containing the user data
     */
    @Operation(summary = "Get user by ID", description = "Fetch a user by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getUserById(
            @Parameter(description = "ID of the user to fetch") @PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            logger.info("Fetched user with ID: {}", userId);
            return ResponseEntityUtil.ok("Fetched user successfully", user);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", userId, e);
            return ResponseEntityUtil.notFound("User not found");
        }
    }

    /**
     * Handles updating the user profile by ID.
     *
     * @param id the user ID
     * @param userUpdateDTO the data transfer object containing user update information
     * @param profileImages the list of uploaded profile images
     * @return the ResponseEntity containing the ApiResponseDTO with the updated user information
     * @throws IOException if an I/O error occurs
     */
    @Operation(
            summary = "Update user by ID",
            description = "Update a user's profile including optional profile image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profile updated successfully",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request, invalid input or file validation error"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping(value = "/users/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateUserById(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "User update details") @Valid @ModelAttribute UserUpdateDTO userUpdateDTO,
            @Parameter(description = "Optional profile image") @RequestParam(value = "profileImage", required = false) List<MultipartFile> profileImages) throws IOException {

        logger.info("Received request to update user profile for ID: {}", id);

        try {
            MultipartFile profileImage = fileValidator.getSingleFile(profileImages);
            if (profileImage != null) {
                fileValidator.validateFile(profileImage);
            }

            UserDTO updatedUser = userService.updateUserById(id, userUpdateDTO, profileImage);
            logger.info("Updated user profile with ID: {}", id);
            return ResponseEntityUtil.ok("Updated user profile", updatedUser);
        } catch (FileValidationException e) {
            logger.error(e.getMessage());
            return ResponseEntityUtil.badRequest(e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.error("User not found with ID: {}", id, e);
            return ResponseEntityUtil.notFound("User not found");
        } catch (IOException e) {
            logger.error("IO Error: {}", e.getMessage(), e);
            return ResponseEntityUtil.internalServerError("Error updating user profile");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntityUtil.internalServerError("Error updating user profile");
        }
    }


    /**
     * Delete a user by their ID.
     *
     * @param id the user ID
     * @return a response entity with a deletion status
     */
    @Operation(
            summary = "Delete user by ID",
            description = "Delete a user by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Error deleting user")
            }
    )
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deleteUserById(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            logger.info("Deleted user with ID: {}", id);
            return ResponseEntityUtil.ok("User profile deleted successfully", "User profile deleted successfully");
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntityUtil.notFound("User not found");
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntityUtil.internalServerError("Error deleting user");
        }
    }

    /**
     * Search users by a keyword with pagination and sorting.
     *
     * @param keyword the search keyword
     * @param page    the page number to retrieve (default is 0)
     * @param size    the number of items per page (default is 10)
     * @param sortBy  the field to sort by (default is "userId")
     * @param sortDir the sort direction (default is "asc")
     * @return a response entity containing a page of users
     */
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponseDTO<Page<UserDTO>>> searchUsers(
            @Parameter(description = "Keyword to search") @RequestParam String keyword,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "userId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            Page<UserDTO> users = userService.searchUsers(keyword, pageable);
            logger.info("Searched users with keyword: {}", keyword);
            return ResponseEntityUtil.page(users, "Searched users");
        } catch (Exception e) {
            logger.error("Error searching users: {}", e.getMessage(), e);
            return ResponseEntityUtil.internalServerError("Error searching users");
        }
    }

    /**
     * Update user roles by their ID.
     *
     * @param id        the user ID
     * @param roleNames the new roles
     * @return a response entity with an update status
     */
    @Operation(
            summary = "Update user roles",
            description = "Update roles for a user by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Roles updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid role names provided"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PostMapping("/users/{id}/roles")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> updateUserRoles(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "Set of role names") @RequestBody Set<String> roleNames) {
        try {
            Set<Role.RoleName> validRoleNames = roleNames.stream()
                    .map(roleName -> {
                        try {
                            return Role.RoleName.valueOf(roleName.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new InvalidRoleNameException("Invalid role name: " + roleName);
                        }
                    })
                    .collect(Collectors.toSet());

            userService.updateUserRoles(id, validRoleNames);
            logger.info("Roles updated for user with ID: {}", id);
            return ResponseEntityUtil.ok("Roles updated successfully", (Map<String, String>) null);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntityUtil.notFound("User not found");
        } catch (InvalidRoleNameException e) {
            logger.error("Invalid role name: {}", e.getMessage());
            Map<String, String> errorData = new HashMap<>();
            errorData.put("message", e.getMessage());
            return ResponseEntityUtil.badRequest("Invalid input", errorData);
        }
    }


    /**
     * Get all users by role with pagination.
     *
     * @param roleName the role name (case-insensitive)
     * @param page the page number (starting from 0)
     * @param size the number of records per page
     * @return a response entity containing a page of users
     */
    @Operation(
            summary = "Get all users by role",
            description = "Fetch all users with a specific role, with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
            }
    )
    @GetMapping("/users/role/{roleName}")
    public ResponseEntity<ApiResponseDTO<Page<UserDTO>>> getAllUsersByRole(
            @Parameter(description = "Role name (case-insensitive)") @PathVariable String roleName,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "userId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            // Additional logging for debugging
            logger.info("Fetching users with role: {} (page: {}, size: {}, sortBy: {}, sortDir: {})", roleName, page, size, sortBy, sortDir);
            Page<UserDTO> usersPage = userService.getAllUsersByRole(roleName, page, size, sortBy, sortDir);
            return ResponseEntityUtil.page(usersPage, "Fetched users successfully");

        } catch (IllegalArgumentException e) {
            // Handle invalid page/size values
            logger.error("Invalid pagination parameters: page={}, size={}", page, size, e);
            return ResponseEntityUtil.badRequest("Invalid pagination parameters");
        }
    }

//    ############################################################# PET #########################################################

    /**
     * Get all pets with pagination and sorting.
     *
     * @param page    the page number to retrieve (default is 0)
     * @param size    the number of items per page (default is 10)
     * @param sortBy  the field to sort by (default is "petId")
     * @param sortDir the sort direction (default is "asc")
     * @return ResponseEntity with ApiResponseDTO containing a page of pets
     */
    @Operation(summary = "Get all pets", description = "Fetch all pets with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pets fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            })
    @GetMapping("/pets")
    public ResponseEntity<ApiResponseDTO<Page<PetResponseDTO>>> getAllPets(
            @Parameter(description = "Page number (starting from 0)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "petId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<PetResponseDTO> pets = petService.getAllPets(pageable);
        logger.info("Fetched all pets with pagination and sorting");
        return ResponseEntity.ok(new ApiResponseDTO<>("Fetched all pets", pets));
    }

    /**
     * Get a pet by its ID.
     *
     * @param id the pet ID
     * @return ResponseEntity with ApiResponseDTO containing the pet
     */
    @Operation(
            summary = "Get pet by ID",
            description = "Fetch a pet by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pet fetched successfully",
                            content = @Content(schema = @Schema(implementation = PetResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Pet not found")
            }
    )
    @GetMapping("/pets/{id}")
    public ResponseEntity<ApiResponseDTO<PetResponseDTO>> getPetById(
            @Parameter(description = "ID of the pet to fetch") @PathVariable Long id) {
        try {
            PetResponseDTO pet = petService.getPetById(id);
            logger.info("Fetched pet with ID: {}", id);
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched pet", pet));
        } catch (ResourceNotFoundException e) {
            logger.error("Pet not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO<>("Pet not found", null));
        }
    }

    /**
     * Update a pet by its ID.
     *
     * @param id         the pet ID
     * @param petRequestDTO     the pet details to update
     * @param avatarFile the avatar file to update (optional)
     * @return ResponseEntity with ApiResponseDTO containing the updated pet
     */
    @Operation(
            summary = "Update pet by ID",
            description = "Update a pet's information, including optional avatar image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pet updated successfully",
                            content = @Content(schema = @Schema(implementation = PetResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Pet not found"),
                    @ApiResponse(responseCode = "500", description = "Error updating pet")
            }
    )
    @PutMapping(value = "/pets/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponseDTO<PetResponseDTO>> updatePetById(
            @Parameter(description = "ID of the pet to update") @PathVariable Long id,
            @Parameter(description = "Pet update data") @Valid @ModelAttribute PetRequestDTO petRequestDTO,
            @Parameter(description = "Optional avatar file") @RequestPart(required = false) MultipartFile avatarFile) {
        try {
            PetResponseDTO updatedPet = petService.updatePetById(id, petRequestDTO, avatarFile);
            logger.info("Updated pet with ID: {}", id);
            return ResponseEntity.ok(new ApiResponseDTO<>("Updated pet", updatedPet));
        } catch (ResourceNotFoundException e) {
            logger.error("Error updating pet with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("Pet not found", null));
        } catch (Exception e) {
            logger.error("Error updating pet with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error updating pet", null));
        }
    }

    /**
     * Delete a pet by its ID.
     *
     * @param id the pet ID
     * @return ResponseEntity with ApiResponse containing the deletion status
     */
    @Operation(
            summary = "Delete pet by ID",
            description = "Delete a pet by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pet deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Pet not found")
            }
    )
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deletePetById(
            @Parameter(description = "ID of the pet to delete") @PathVariable Long id) {
        try {
            petService.deletePetById(id);
            logger.info("Deleted pet with ID: {}", id);
            return ResponseEntity.ok(new ApiResponseDTO<>("Pet deleted successfully"));
        } catch (PetNotFoundException e) {
            logger.error("Pet not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO<>("Pet not found", null));
        }
    }

    //    ############################################################# SPECIALIST #########################################################

    /**
     * Create a new specialist.
     * Consumes multipart/form-data for profile image.
     *
     * @param specialistCreateRequestDTO   SpecialistCreateRequestDTO containing the specialist's details
     * @param profileImage Profile image file of the specialist (optional)
     * @param bindingResult BindingResult for validation errors
     * @return ResponseEntity with ApiResponseDTO containing the created specialist's details
     */
    @Operation(summary = "Create a specialist", description = "Create a new specialist with optional profile image",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Specialist created successfully",
                            content = @Content(schema = @Schema(implementation = SpecialistRegistrationResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "409", description = "Specialist already exists")
            })
    @PostMapping(value = "/specialists", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponseDTO<SpecialistRegistrationResponseDTO>> createSpecialist(
            @Valid @ModelAttribute SpecialistCreateRequestDTO specialistCreateRequestDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            logger.error("Validation error while creating specialist: {}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>("Validation error: " + errorMessage, null));
        }

        try {
            specialistService.createSpecialistByAdmin(specialistCreateRequestDTO, profileImage);
            String responseMessage = "Created specialist profile for: " + specialistCreateRequestDTO.getEmail();
            logger.info(responseMessage);
            SpecialistRegistrationResponseDTO response = new SpecialistRegistrationResponseDTO(responseMessage);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>("Specialist created successfully.", response));
        } catch (UserAlreadyExistsException e) {
            logger.error("Specialist already exists with email: {}", specialistCreateRequestDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO<>("Specialist already exists with this email.", null));
        } catch (IOException e) {
            logger.error("IO Error during specialist creation for email: {}", specialistCreateRequestDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("IO Error during specialist creation: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error creating specialist profile for: {}", specialistCreateRequestDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error creating specialist profile: " + e.getMessage(), null));
        }
    }



    /**
     * Update an existing specialist by admin.
     * Consumes multipart/form-data for profile image.
     *
     * @param id Specialist ID
     * @param specialistUpdateRequestDTO DTO containing specialist information
     * @param profileImages List of profile image files
     * @param bindingResult BindingResult for validation errors
     * @return ResponseEntity with ApiResponse containing the updated specialist
     */
    @Operation(
            summary = "Update specialist by admin",
            description = "Update an existing specialist's profile by admin, including optional profile image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specialist updated successfully",
                            content = @Content(schema = @Schema(implementation = SpecialistResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or file validation error"),
                    @ApiResponse(responseCode = "404", description = "Specialist not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping(value = "/specialists/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseDTO<SpecialistResponseDTO>> updateSpecialistByAdmin(
            @Parameter(description = "ID of the specialist to update") @PathVariable Long id,
            @Parameter(description = "Specialist update details") @Valid @ModelAttribute SpecialistUpdateRequestDTO specialistUpdateRequestDTO,
            @Parameter(description = "Optional profile image") @RequestPart(value = "profileImage", required = false) List<MultipartFile> profileImages,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            logger.error("Validation error while updating specialist: {}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>("Validation error: " + errorMessage, null));
        }

        logger.info("Received request to update specialist profile for ID: {}", id);

        try {
            MultipartFile profileImage = fileValidator.getSingleFile(profileImages);
            if (profileImage != null) {
                fileValidator.validateFile(profileImage);
            }

            SpecialistResponseDTO specialist = specialistService.updateSpecialistByAdmin(id, specialistUpdateRequestDTO, profileImage);
            logger.info("Updated specialist profile with ID: {}", id);
            return ResponseEntity.ok(new ApiResponseDTO<>("Updated specialist profile", specialist));
        } catch (FileValidationException e) {
            logger.error("File validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("Specialist not found", null));
        } catch (IOException e) {
            logger.error("IO Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error updating specialist profile", null));
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt by user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO<>("Unauthorized access", null));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error updating specialist profile", null));
        }
    }


    /**
     * Search for specialists with a keyword.
     * Supports pagination and sorting.
     *
     * @param keyword the search keyword
     * @param page    the page number to retrieve (default is 0)
     * @param size    the number of items per page (default is 10)
     * @param sortBy  the field to sort by (default is "specialistId")
     * @param sortDir the sort direction (default is "asc")
     * @return ResponseEntity with ApiResponse containing a page of specialists
     */
    @Operation(
            summary = "Search specialists",
            description = "Search for specialists by keyword with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specialists fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "No specialists found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    @GetMapping("/specialists/search")
    public ResponseEntity<ApiResponseDTO<Page<SpecialistResponseDTO>>> searchSpecialists(
            @Parameter(description = "Keyword to search") @RequestParam String keyword,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "specialistId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            Page<SpecialistResponseDTO> specialists = specialistService.searchSpecialists(keyword, pageable);
            logger.info("Searched specialists with keyword: {}", keyword);
            return ResponseEntity.ok(new ApiResponseDTO<>("Searched specialists", specialists));
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found with keyword {}: {}", keyword, e.getMessage());
            return new ResponseEntity<>(new ApiResponseDTO<>("No specialists found with the keyword: " + keyword), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt for keyword search: {}", keyword, e.getMessage());
            return new ResponseEntity<>(new ApiResponseDTO<>("Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Delete a specialist by their ID.
     *
     * @param id the specialist ID
     * @return ResponseEntity with ApiResponse containing the deletion status
     */
    @Operation(
            summary = "Delete specialist by admin",
            description = "Delete a specialist profile by admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Specialist deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Specialist not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "500", description = "IO error or internal server error")
            }
    )
    @DeleteMapping("/specialists/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deleteSpecialistByAdmin(
            @Parameter(description = "ID of the specialist to delete") @PathVariable Long id) {
        try {
            specialistService.deleteSpecialistByAdmin(id);
            logger.info("Deleted specialist with ID: {}", id);
            return ResponseEntity.ok(new ApiResponseDTO<>("Specialist profile deleted successfully"));
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponseDTO<>("Specialist not found"), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access attempt to delete specialist with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponseDTO<>("Unauthorized access"), HttpStatus.UNAUTHORIZED);
        } catch (IOException e) {
            logger.error("IO Error deleting specialist with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponseDTO<>("IO Error deleting specialist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //    ############################################################# FORUM #########################################################

    /**
     * Update a forum by its ID.
     *
     * @param forumId        the forum ID
     * @param updateForumDTO the forum update data
     * @return the updated forum details
     */
    @Operation(summary = "Update forum", description = "Update a forum by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Forum updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Forum not found")
            })
    @PutMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<ForumDTO>> updateForumById(
            @Parameter(description = "ID of the forum to update") @PathVariable String forumId,
            @Parameter(description = "Forum update data") @RequestBody UpdateForumDTO updateForumDTO) {
        try {
            ForumDTO updatedForum = forumService.updateForumById(forumId, updateForumDTO);
            ApiResponseDTO<ForumDTO> apiResponseDTO = new ApiResponseDTO<>("Forum updated successfully", updatedForum);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ResourceNotFoundException e) {
            ApiResponseDTO<ForumDTO> response = new ApiResponseDTO<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Delete a forum by its ID.
     *
     * @param forumId the forum ID
     * @return a response indicating the result of the deletion
     */
    @Operation(
            summary = "Delete forum by ID",
            description = "Delete a forum by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Forum deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Forum not found")
            }
    )
    @DeleteMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteForumById(
            @Parameter(description = "ID of the forum to delete") @PathVariable String forumId) {
        try {
            forumService.deleteForumById(forumId);
            ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>("Forum deleted successfully", null);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ResourceNotFoundException e) {
            ApiResponseDTO<Void> response = new ApiResponseDTO<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //    ############################################################# COMMENT #########################################################

    /**
     * Fetch all comments with pagination.
     *
     * @param page the page number (default is 0)
     * @param size the page size (default is 5)
     * @return ResponseEntity with ApiResponse containing a paginated list of comments
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponseDTO<Page<CommentDTO>>> getAllComments(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        logger.debug("Fetching all comments with pagination");
        Page<CommentDTO> comments = commentService.getAllComments(PageRequest.of(page, size))
                .map(commentMapper::toDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>("Comments fetched successfully", comments));
    }

    /**
     * Fetch a comment by its ID.
     *
     * @param commentId the comment ID
     * @return ResponseEntity with ApiResponse containing the comment with the specified ID
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> getCommentById(@PathVariable String commentId) {
        logger.debug("Fetching comment with ID: {}", commentId);
        Optional<Comment> comment = commentService.getCommentById(commentId);
        return comment.map(value -> ResponseEntity.ok(new ApiResponseDTO<>("Comment fetched successfully", commentMapper.toDTO(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDTO<>("Comment not found", null)));
    }

    /**
     * Update a comment by its ID.
     *
     * @param commentId  the comment ID
     * @param commentDTO the updated comment data
     * @return ResponseEntity with ApiResponse containing the updated comment
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDTO<CommentDTO>> updateCommentById(@PathVariable String commentId, @Valid @RequestBody CommentDTO commentDTO) {
        logger.debug("Updating comment with ID: {}", commentId);
        Optional<CommentDTO> updatedComment = commentService.updateCommentByIdAdmin(commentId, commentDTO);
        return updatedComment.map(value -> ResponseEntity.ok(new ApiResponseDTO<>("Comment updated successfully", value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDTO<>("Comment not found", null)));
    }

    /**
     * Delete a comment by its ID.
     *
     * @param commentId the comment ID
     * @return ResponseEntity with ApiResponse indicating the result of the deletion
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCommentByIdAdmin(@PathVariable String commentId) {
        logger.debug("Deleting comment with ID: {}", commentId);
        boolean deleted = commentService.deleteCommentById(commentId);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponseDTO<>("Comment deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("Comment not found", null));
        }
    }

    //    ############################################################# Like #########################################################

    /**
     * Fetch all likes.
     *
     * @return ResponseEntity with ApiResponse containing a list of likes
     */
    @GetMapping("/likes")
    public ResponseEntity<ApiResponseDTO<List<LikeDTO>>> getAllLikes() {
        logger.debug("Fetching all likes");
        List<LikeDTO> likes = likeService.getAllLikes().stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseDTO<>("Likes fetched successfully", likes));
    }

    /**
     * Fetch a like by its ID.
     *
     * @param likeId the like ID
     * @return ResponseEntity with ApiResponse containing the like
     */
    @GetMapping("/likes/{likeId}")
    public ResponseEntity<ApiResponseDTO<LikeDTO>> getLikeById(@PathVariable String likeId) {
        logger.debug("Fetching like with ID: {}", likeId);
        Optional<Like> like = likeService.getLikeById(likeId);
        return like.map(value -> ResponseEntity.ok(new ApiResponseDTO<>("Like fetched successfully", likeMapper.toDTO(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDTO<>("Like not found", null)));
    }

    /**
     * Fetch likes for a comment by its ID.
     *
     * @param commentId the comment ID
     * @return ResponseEntity with ApiResponse containing a list of likes for the specified comment
     */
    @GetMapping("/comment/{commentId}/likes")
    public ResponseEntity<ApiResponseDTO<List<LikeDTO>>> getLikesByCommentId(@PathVariable String commentId) {
        logger.debug("Fetching likes for comment with ID: {}", commentId);
        List<LikeDTO> likes = likeService.getAllLikesByCommentId(commentId).stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponseDTO<>("Likes fetched successfully", likes));
    }

}
