package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Comment;
import com.petconnect.backend.entity.Like;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
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
import java.util.Map;
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

    // Endpoint to get all users with pagination and sorting
    @GetMapping("/users")
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

    // Endpoint to get a user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            logger.info("Fetched user with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Fetched user", user));
        } catch (Exception e) {
            logger.error("Error fetching user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("User not found"), HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to delete a user by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            logger.info("Deleted user with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("User profile deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error deleting user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to update a user profile
    @PutMapping("/users/{id}")
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
        } catch (Exception e) {
            logger.error("Error updating user profile with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error updating user profile"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to search for users by keyword with pagination and sorting
    @GetMapping("/users/search")
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

    @PostMapping("/users/{id}/roles")
    public ResponseEntity<ApiResponse<String>> updateUserRoles(
            @PathVariable Long id, @RequestBody Set<Role.RoleName> roleNames) {
        try {
            userService.updateUserRoles(id, roleNames);
            logger.info("Roles updated for user with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Roles updated successfully"));
        } catch (Exception e) {
            logger.error("Error updating roles for user with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error updating roles"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    ######################################################################################################################

    @PostMapping(value = "specialists",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

        SpecialistDTO createdSpecialist = specialistService.createSpecialistByAdmin(requestDTO, profileImage);

        return ResponseEntity.ok(new ApiResponse<>("Specialist created successfully. Verification email sent.", createdSpecialist));
    }

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

//    ######################################################################################################################

    @GetMapping("/pets")
    public Page<PetDTO> getAllPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return petService.getAllPets(page, size);
    }

    @GetMapping("/pets/{id}")
    public PetDTO getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @PutMapping("/pets/{id}")
    public PetDTO updatePetById(@PathVariable Long id, @Valid @RequestPart PetDTO petDTO,
                            @RequestPart(required = false) MultipartFile avatarFile) throws IOException {
        return petService.updatePetById(id, petDTO, avatarFile);
    }

    @DeleteMapping("/pets/{id}")
    public void deletePetById(@PathVariable Long id) {
        petService.deletePetById(id);
    }

//    ########################################################################################################
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


    //    ######################################################################################################################

    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<List<LikeDTO>>> getAllLikes() {
        logger.debug("Fetching all likes");
        List<LikeDTO> likes = likeService.getAllLikes().stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Likes fetched successfully", likes));
    }

    @GetMapping("/likes/{likeId}")
    public ResponseEntity<ApiResponse<LikeDTO>> getLikeById(@PathVariable String likeId) {
        logger.debug("Fetching like with ID: {}", likeId);
        Optional<Like> like = likeService.getLikeById(likeId);
        return like.map(value -> ResponseEntity.ok(new ApiResponse<>("Like fetched successfully", likeMapper.toDTO(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Like not found", null)));
    }

    /**
     * Fetch likes for a comment by its ID.
     *
     * @param commentId the comment ID
     * @return a response containing a list of likes for the specified comment
     */
    @GetMapping("/comment/{commentId}/likes")
    public ResponseEntity<ApiResponse<List<LikeDTO>>> getLikesByCommentId(@PathVariable String commentId) {
        List<LikeDTO> likes = likeService.getAllLikesByCommentId(commentId).stream()
                .map(likeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Likes fetched successfully", likes));
    }
    //    ######################################################################################################################

    /**
     * Fetch all comments with pagination.
     *
     * @param page the page number (default is 0)
     * @param size the page size (default is 5)
     * @return a paginated list of comments
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Page<CommentDTO>>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        logger.debug("Fetching all comments with pagination");
        Page<CommentDTO> comments = commentService.getAllComments(PageRequest.of(page, size))
                .map(commentMapper::toDTO);
        return ResponseEntity.ok(new ApiResponse<>("Comments fetched successfully", comments));
    }

    /**
     * Fetch a comment by its ID.
     *
     * @param commentId the comment ID
     * @return the comment with the specified ID
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentById(@PathVariable String commentId) {
        logger.debug("Fetching comment with ID: {}", commentId);
        Optional<Comment> comment = commentService.getCommentById(commentId);
        return comment.map(value -> ResponseEntity.ok(new ApiResponse<>("Comment fetched successfully", commentMapper.toDTO(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Comment not found", null)));
    }

    /**
     * Update a comment by its ID.
     *
     * @param commentId the comment ID
     * @param commentDTO the updated comment data
     * @return the updated comment
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> updateCommentById(@PathVariable String commentId, @Valid @RequestBody CommentDTO commentDTO) {
        logger.debug("Updating comment with ID: {}", commentId);
        Optional<CommentDTO> updatedComment = commentService.updateCommentByIdAdmin(commentId, commentDTO);
        if (updatedComment.isPresent()) {
            ApiResponse<CommentDTO> apiResponse = new ApiResponse<>("Comment updated successfully", updatedComment.get());
            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<CommentDTO> apiResponse = new ApiResponse<>("Comment not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * Delete a comment by its ID.
     *
     * @param commentId the comment ID
     * @return a response indicating the result of the deletion
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteCommentByIdAdmin(@PathVariable String commentId) {
        logger.debug("Deleting comment with ID: {}", commentId);
        boolean deleted = commentService.deleteCommentById(commentId);
        if (deleted) {
            ApiResponse<Void> apiResponse = new ApiResponse<>("Comment deleted successfully", null);
            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Void> apiResponse = new ApiResponse<>("Comment not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * Delete any comment by its ID.
     *
     * @param commentId the comment ID
     * @return a response indicating the result of the deletion
     */
//    @DeleteMapping("/comments/{commentId}")
//    public ResponseEntity<ApiResponse<Void>> deleteAnyComment(@PathVariable String commentId) {
//        try {
//            commentService.deleteAnyComment(commentId);
//            ApiResponse<Void> apiResponse = new ApiResponse<>("Comment deleted successfully", null);
//            return ResponseEntity.ok(apiResponse);
//        } catch (ResourceNotFoundException e) {
//            ApiResponse<Void> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
}
