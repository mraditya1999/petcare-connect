package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.Like;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.exceptions.PetNotFoundException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserNotFoundException;
import com.petconnect.backend.mappers.CommentMapper;
import com.petconnect.backend.mappers.LikeMapper;
import com.petconnect.backend.services.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PetService petService;
    private final SpecialistService specialistService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final ForumService forumService;
    private final LikeService likeService;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;

    @Autowired
    public AdminController(UserService userService, PetService petService, SpecialistService specialistService, ForumService forumService, LikeService likeService, LikeMapper likeMapper, CommentMapper commentMapper, CommentService commentService) {
        this.userService = userService;
        this.petService = petService;
        this.specialistService = specialistService;
        this.forumService = forumService;
        this.likeService = likeService;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
    }

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
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            Page<UserDTO> users = userService.getAllUsers(pageable);
            logger.info("Fetched all users with pagination and sorting");
            return ResponseEntity.ok(new ApiResponse<>("Fetched all users", users));
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching users"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a user by their ID.
     *
     * @param id the user ID
     * @return a response entity containing the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            logger.info("Fetched user with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Fetched user", user));
        } catch (UserNotFoundException e) {
            logger.error("Error fetching user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a user by their ID.
     *
     * @param id the user ID
     * @return a response entity with a deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            logger.info("Deleted user with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("User profile deleted successfully"));
        } catch (UserNotFoundException e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error deleting user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update a user by their ID.
     *
     * @param id           the user ID
     * @param firstName    the new first name
     * @param lastName     the new last name
     * @param email        the new email
     * @param mobileNumber the new mobile number
     * @param pincode      the new pincode
     * @param city         the new city
     * @param state        the new state
     * @param country      the new country
     * @param locality     the new locality
     * @param profileImage the new profile image
     * @return a response entity containing the updated user
     * @throws IOException if an error occurs while processing the profile image
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserById(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) Long pincode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) MultipartFile profileImage) throws IOException {
        try {
            UserDTO updatedUser = userService.updateUserById(
                    id, firstName, lastName, email, mobileNumber, pincode, city, state, country, locality, profileImage);
            logger.info("Updated user profile with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Updated user profile", updatedUser));
        } catch (UserNotFoundException e) {
            logger.error("Error updating user profile with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating user profile with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error updating user profile"), HttpStatus.INTERNAL_SERVER_ERROR);
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
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            Page<UserDTO> users = userService.searchUsers(keyword, pageable);
            logger.info("Searched users with keyword: {}", keyword);
            return ResponseEntity.ok(new ApiResponse<>("Searched users", users));
        } catch (Exception e) {
            logger.error("Error searching users with keyword {}: {}", keyword, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error searching users"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update user roles by their ID.
     *
     * @param id        the user ID
     * @param roleNames the new roles
     * @return a response entity with an update status
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<String>> updateUserRoles(
            @PathVariable Long id, @RequestBody Set<Role.RoleName> roleNames) {
        try {
            userService.updateUserRoles(id, roleNames);
            logger.info("Roles updated for user with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Roles updated successfully"));
        } catch (UserNotFoundException e) {
            logger.error("Error updating roles for user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating roles for user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error updating roles"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    ############################################################# SPECIALIST #########################################################

    /**
     * Create a new specialist.
     * Consumes multipart/form-data for profile image.
     *
     * @param firstName    Specialist's first name
     * @param lastName     Specialist's last name
     * @param email        Specialist's email
     * @param password     Specialist's password
     * @param mobileNumber Specialist's mobile number
     * @param speciality   Specialist's speciality
     * @param about        Information about the specialist
     * @param profileImage Profile image file
     * @param pincode      Address pincode
     * @param city         Address city
     * @param state        Address state
     * @param locality     Address locality
     * @param country      Address country
     * @return ResponseEntity with ApiResponse containing the created specialist
     * @throws IOException if there's an error processing the profile image
     */
    @PostMapping(value = "specialists", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SpecialistDTO>> createSpecialist(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam("speciality") String speciality,
            @RequestParam("about") String about,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam("pincode") Long pincode,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("locality") String locality,
            @RequestParam("country") String country) throws IOException {

        // Create and populate the request DTO
        SpecialistCreateRequestDTO requestDTO = new SpecialistCreateRequestDTO();
        requestDTO.setFirstName(firstName);
        requestDTO.setLastName(lastName);
        requestDTO.setEmail(email);
        requestDTO.setPassword(password);
        requestDTO.setMobileNumber(mobileNumber);
        requestDTO.setSpeciality(speciality);
        requestDTO.setAbout(about);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setPincode(pincode);
        addressDTO.setCity(city);
        addressDTO.setState(state);
        addressDTO.setLocality(locality);
        addressDTO.setCountry(country);
        requestDTO.setAddressDTO(addressDTO);

        // Create the specialist
        SpecialistDTO createdSpecialist = specialistService.createSpecialistByAdmin(requestDTO, profileImage);
        logger.info("Created specialist profile for: {}", email);

        return ResponseEntity.ok(new ApiResponse<>("Specialist created successfully. Verification email sent.", createdSpecialist));
    }

    /**
     * Update an existing specialist by admin.
     * Consumes multipart/form-data for profile image.
     *
     * @param id           Specialist ID
     * @param firstName    Specialist's first name
     * @param lastName     Specialist's last name
     * @param email        Specialist's email
     * @param mobileNumber Specialist's mobile number
     * @param speciality   Specialist's speciality
     * @param about        Information about the specialist
     * @param pincode      Address pincode
     * @param city         Address city
     * @param state        Address state
     * @param locality     Address locality
     * @param country      Address country
     * @param password     Specialist's password
     * @param profileImage Profile image file
     * @return ResponseEntity with ApiResponse containing the updated specialist
     * @throws IOException if there's an error processing the profile image
     */
    @PutMapping(value = "/specialists/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SpecialistDTO>> updateSpecialistByAdmin(
            @PathVariable Long id,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "speciality", required = false) String speciality,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam(value = "pincode", required = false) Long pincode,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "locality", required = false) String locality,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "password", required = false) String password,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        // Create and populate the update request DTO
        SpecialistUpdateRequestDTO specialistUpdateRequestDTO = new SpecialistUpdateRequestDTO();
        specialistUpdateRequestDTO.setFirstName(firstName);
        specialistUpdateRequestDTO.setLastName(lastName);
        specialistUpdateRequestDTO.setEmail(email);
        specialistUpdateRequestDTO.setMobileNumber(mobileNumber);
        specialistUpdateRequestDTO.setSpeciality(speciality);
        specialistUpdateRequestDTO.setAbout(about);
        specialistUpdateRequestDTO.setPassword(password);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setPincode(pincode);
        addressDTO.setCity(city);
        addressDTO.setState(state);
        addressDTO.setLocality(locality);
        addressDTO.setCountry(country);
        specialistUpdateRequestDTO.setAddressDTO(addressDTO);

        try {
            SpecialistDTO specialist = specialistService.updateSpecialistByAdmin(id, specialistUpdateRequestDTO, profileImage);
            logger.info("Updated specialist profile with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Updated specialist profile", specialist));
        } catch (Exception e) {
            logger.error("Error updating specialist profile with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error updating specialist profile"), HttpStatus.INTERNAL_SERVER_ERROR);
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
    @GetMapping("/specialists/search")
    public ResponseEntity<ApiResponse<Page<SpecialistDTO>>> searchSpecialists(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "specialistId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            Page<SpecialistDTO> specialists = specialistService.searchSpecialists(keyword, pageable);
            logger.info("Searched specialists with keyword: {}", keyword);
            return ResponseEntity.ok(new ApiResponse<>("Searched specialists", specialists));
        } catch (Exception e) {
            logger.error("Error searching specialists with keyword {}: {}", keyword, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error searching specialists"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a specialist by their ID.
     *
     * @param id the specialist ID
     * @return ResponseEntity with ApiResponse containing the deletion status
     */
    @DeleteMapping("/specialists/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSpecialistByAdmin(@PathVariable Long id) {
        try {
            specialistService.deleteSpecialistByAdmin(id);
            logger.info("Deleted specialist with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Specialist profile deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting specialist with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error deleting specialist"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    ############################################################# PET #########################################################

    /**
     * Get all pets with pagination and sorting.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of items per page (default is 10)
     * @param sortBy the field to sort by (default is "id")
     * @param sortDir the sort direction (default is "asc")
     * @return ResponseEntity with ApiResponse containing a page of pets
     */
    @GetMapping("/pets")
    public ResponseEntity<ApiResponse<Page<PetDTO>>> getAllPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            Page<PetDTO> pets = petService.getAllPets(pageable);
            logger.info("Fetched all pets with pagination and sorting");
            return ResponseEntity.ok(new ApiResponse<>("Fetched all pets", pets));
        } catch (Exception e) {
            logger.error("Error fetching pets: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching pets"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get a pet by its ID.
     *
     * @param id the pet ID
     * @return ResponseEntity with ApiResponse containing the pet
     */
    @GetMapping("/pets/{id}")
    public ResponseEntity<ApiResponse<PetDTO>> getPetById(@PathVariable Long id) {
        try {
            PetDTO pet = petService.getPetById(id);
            logger.info("Fetched pet with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Fetched pet", pet));
        } catch (PetNotFoundException e) {
            logger.error("Error fetching pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Pet not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching pet"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update a pet by its ID.
     *
     * @param id         the pet ID
     * @param petDTO     the pet details to update
     * @param avatarFile the avatar file to update (optional)
     * @return ResponseEntity with ApiResponse containing the updated pet
     * @throws IOException if there's an error processing the avatar file
     */
    @PutMapping(value = "/pets/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PetDTO>> updatePetById(@PathVariable Long id,
                                                             @Valid @RequestPart PetDTO petDTO,
                                                             @RequestPart(required = false) MultipartFile avatarFile) throws IOException {
        try {
            PetDTO updatedPet = petService.updatePetById(id, petDTO, avatarFile);
            logger.info("Updated pet with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Updated pet", updatedPet));
        } catch (PetNotFoundException e) {
            logger.error("Error updating pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Pet not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error updating pet"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a pet by its ID.
     *
     * @param id the pet ID
     * @return ResponseEntity with ApiResponse containing the deletion status
     */
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<ApiResponse<String>> deletePetById(@PathVariable Long id) {
        try {
            petService.deletePetById(id);
            logger.info("Deleted pet with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Pet deleted successfully"));
        } catch (PetNotFoundException e) {
            logger.error("Error deleting pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Pet not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error deleting pet"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    ############################################################# FORUM #########################################################

    /**
     * Update a forum by its ID.
     *
     * @param forumId the forum ID
     * @param updateForumDTO the forum update data
     * @return the updated forum details
     */
    @PutMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponse<ForumDTO>> updateForumById(@PathVariable String forumId, @RequestBody UpdateForumDTO updateForumDTO) {
        try {
            ForumDTO updatedForum = forumService.updateForumById(forumId, updateForumDTO);
            ApiResponse<ForumDTO> apiResponse = new ApiResponse<>("Forum updated successfully", updatedForum);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            ApiResponse<ForumDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Delete a forum by its ID.
     *
     * @param forumId the forum ID
     * @return a response indicating the result of the deletion
     */
    @DeleteMapping("/forums/{forumId}")
    public ResponseEntity<ApiResponse<Void>> deleteForumById(@PathVariable String forumId) {
        try {
            forumService.deleteForumById(forumId);
            ApiResponse<Void> apiResponse = new ApiResponse<>("Forum deleted successfully", null);
            return ResponseEntity.ok(apiResponse);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
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
    public ResponseEntity<ApiResponse<Page<CommentDTO>>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            logger.debug("Fetching all comments with pagination");
            Page<CommentDTO> comments = commentService.getAllComments(PageRequest.of(page, size))
                    .map(commentMapper::toDTO);
            return ResponseEntity.ok(new ApiResponse<>("Comments fetched successfully", comments));
        } catch (Exception e) {
            logger.error("Error fetching comments: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching comments"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch a comment by its ID.
     *
     * @param commentId the comment ID
     * @return ResponseEntity with ApiResponse containing the comment with the specified ID
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentById(@PathVariable String commentId) {
        try {
            logger.debug("Fetching comment with ID: {}", commentId);
            Optional<Comment> comment = commentService.getCommentById(commentId);
            return comment.map(value -> ResponseEntity.ok(new ApiResponse<>("Comment fetched successfully", commentMapper.toDTO(value))))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>("Comment not found", null)));
        } catch (Exception e) {
            logger.error("Error fetching comment with ID {}: {}", commentId, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching comment"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update a comment by its ID.
     *
     * @param commentId the comment ID
     * @param commentDTO the updated comment data
     * @return ResponseEntity with ApiResponse containing the updated comment
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> updateCommentById(@PathVariable String commentId, @Valid @RequestBody CommentDTO commentDTO) {
        try {
            logger.debug("Updating comment with ID: {}", commentId);
            Optional<CommentDTO> updatedComment = commentService.updateCommentByIdAdmin(commentId, commentDTO);
            return updatedComment.map(value -> ResponseEntity.ok(new ApiResponse<>("Comment updated successfully", value)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>("Comment not found", null)));
        } catch (Exception e) {
            logger.error("Error updating comment with ID {}: {}", commentId, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error updating comment"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a comment by its ID.
     *
     * @param commentId the comment ID
     * @return ResponseEntity with ApiResponse indicating the result of the deletion
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteCommentByIdAdmin(@PathVariable String commentId) {
        try {
            logger.debug("Deleting comment with ID: {}", commentId);
            boolean deleted = commentService.deleteCommentById(commentId);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<>("Comment deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Comment not found", null));
            }
        } catch (Exception e) {
            logger.error("Error deleting comment with ID {}: {}", commentId, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error deleting comment"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    ############################################################# Like #########################################################

    /**
     * Fetch all likes.
     *
     * @return ResponseEntity with ApiResponse containing a list of likes
     */
    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<List<LikeDTO>>> getAllLikes() {
        try {
            logger.debug("Fetching all likes");
            List<LikeDTO> likes = likeService.getAllLikes().stream()
                    .map(likeMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("Likes fetched successfully", likes));
        } catch (Exception e) {
            logger.error("Error fetching all likes: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching likes"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch a like by its ID.
     *
     * @param likeId the like ID
     * @return ResponseEntity with ApiResponse containing the like
     */
    @GetMapping("/likes/{likeId}")
    public ResponseEntity<ApiResponse<LikeDTO>> getLikeById(@PathVariable String likeId) {
        try {
            logger.debug("Fetching like with ID: {}", likeId);
            Optional<Like> like = likeService.getLikeById(likeId);
            return like.map(value -> ResponseEntity.ok(new ApiResponse<>("Like fetched successfully", likeMapper.toDTO(value))))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>("Like not found", null)));
        } catch (Exception e) {
            logger.error("Error fetching like with ID {}: {}", likeId, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching like"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch likes for a comment by its ID.
     *
     * @param commentId the comment ID
     * @return ResponseEntity with ApiResponse containing a list of likes for the specified comment
     */
    @GetMapping("/comment/{commentId}/likes")
    public ResponseEntity<ApiResponse<List<LikeDTO>>> getLikesByCommentId(@PathVariable String commentId) {
        try {
            logger.debug("Fetching likes for comment with ID: {}", commentId);
            List<LikeDTO> likes = likeService.getAllLikesByCommentId(commentId).stream()
                    .map(likeMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("Likes fetched successfully", likes));
        } catch (Exception e) {
            logger.error("Error fetching likes for comment with ID {}: {}", commentId, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching likes"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
