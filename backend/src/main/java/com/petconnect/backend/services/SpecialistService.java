package com.petconnect.backend.services;

import com.petconnect.backend.dto.TempUserDTO;
import com.petconnect.backend.dto.specialist.SpecialistResponseDTO;
import com.petconnect.backend.dto.specialist.SpecialistUpdateRequestDTO;
import com.petconnect.backend.dto.specialist.SpecialistCreateRequestDTO;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.mappers.TempUserMapper;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import com.petconnect.backend.utils.TempUserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Service
public class SpecialistService {
    @Value("${verification.token.ttl.hours:24}")
    private long verificationTtlHours;

    private static final Logger logger = LoggerFactory.getLogger(SpecialistService.class);

    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final RoleAssignmentUtil roleAssignmentUtil;
    private final PasswordEncoder passwordEncoder;
    private final SpecialistMapper specialistMapper;
    private final UploadService uploadService;
    private final VerificationService verificationService;
    private final TempUserStore tempUserStore;
    private final TempUserMapper tempUserMapper;


    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, RoleAssignmentUtil roleAssignmentUtil,
                             @Lazy PasswordEncoder passwordEncoder,
                             SpecialistMapper specialistMapper, UploadService uploadService, VerificationService verificationService, TempUserStore tempUserStore, TempUserMapper tempUserMapper) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.passwordEncoder = passwordEncoder;
        this.specialistMapper = specialistMapper;
        this.uploadService = uploadService;
        this.verificationService = verificationService;
        this.tempUserStore = tempUserStore;
        this.tempUserMapper = tempUserMapper;
    }

    /**
     * Retrieves all specialists with pagination.
     *
     * @param pageable Pageable object for pagination
     * @return A page of SpecialistDTO objects
     */
    public Page<SpecialistResponseDTO> getAllSpecialists(Pageable pageable) {
        try {
            Page<Specialist> specialists = specialistRepository.findAll(pageable);
            logger.info("Fetched all specialists with pagination");
            return specialists.map(specialistMapper::toDTO);
        } catch (Exception e) {
            logger.error("Error fetching specialists: {}", e.getMessage());
            throw new RuntimeException("Error fetching specialists", e);
        }
    }

    /**
     * Retrieves a specialist by ID.
     *
     * @param id The ID of the specialist to retrieve
     * @return A SpecialistDTO object
     * @throws ResourceNotFoundException if the specialist is not found
     */
    public SpecialistResponseDTO getSpecialistById(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found"));
        logger.info("Fetching specialist with ID: {}", id);
        return specialistMapper.toDTO(specialist);
    }

    /**
     * Updates the current specialist's profile.
     *
     * @param specialistUpdateRequestDTO DTO containing specialist information
     * @param profileImage The profile image file
     * @param userDetails Authenticated user's details
     * @return The updated SpecialistDTO object
     * @throws IOException If an error occurs during image upload
     */
    @Transactional
    public SpecialistResponseDTO updateSpecialist(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage, UserDetails userDetails) throws IOException {
        Specialist specialist = (Specialist) userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + userDetails.getUsername()));
        logger.info("Updating specialist profile for user: {}", userDetails.getUsername());
        return updateSpecialist(specialist, specialistUpdateRequestDTO, profileImage);
    }

    //    ADMIN SERVICES
    /**
     * Creates a new specialist profile by admin.
     *
     * @param specialistCreateRequestDTO DTO containing specialist creation information
     * @param profileImage The profile image file
     * @throws IOException If an error occurs during image upload
     */
    @Transactional
    public void createSpecialistByAdmin(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) throws IOException {
        if (userRepository.existsByEmail(specialistCreateRequestDTO.getEmail())) {
            logger.warn("Specialist already exists with email: {}", specialistCreateRequestDTO.getEmail());
            throw new UserAlreadyExistsException("Specialist already exists with this email.");
        }

        Specialist specialist = specialistMapper.toSpecialistEntity(specialistCreateRequestDTO);
        specialist.setPassword(passwordEncoder.encode(specialistCreateRequestDTO.getPassword()));
        specialist.setVerificationToken(UUID.randomUUID().toString());
        specialist.setVerified(false);  // Set verified to false for admin-created specialists

        // Since specialist is created by admin, manually assign USER and SPECIALIST roles
        Set<Role.RoleName> roles = new HashSet<>();
        roles.add(Role.RoleName.USER);
        roles.add(Role.RoleName.SPECIALIST);
        roleAssignmentUtil.assignRoles(specialist, roles);

        // Handle profile image upload if it exists
        handleProfileImageUpload(profileImage, specialist);

        // Temporarily save the specialist
        TempUserDTO tempUserDTO = tempUserMapper.toTempUserDTO(specialist);

        Duration ttl = Duration.ofHours(verificationTtlHours);
        tempUserStore.saveTemporaryUser(specialist.getVerificationToken(), tempUserDTO,ttl);
        verificationService.sendVerificationEmail(specialist);
        logger.info("Specialist created by admin with email: {}", specialist.getEmail());
    }

   /**
     * Updates a specialist by admin.
     *
     * @param id Specialist ID
     * @param specialistUpdateRequestDTO DTO containing specialist information
     * @param profileImage Profile image file
     * @return The updated specialistResponseDTO object
     * @throws IOException If an error occurs during image upload
     */
   @Transactional
   public SpecialistResponseDTO updateSpecialistByAdmin(Long id, SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage) throws IOException {
       Specialist specialist = specialistRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
       logger.info("Updating specialist profile with ID: {}", id);
       return updateSpecialist(specialist, specialistUpdateRequestDTO, profileImage);
   }


    /**
     * Updates the specialist profile.
     *
     * @param specialist The specialist entity to update
     * @param specialistUpdateRequestDTO DTO containing specialist information
     * @param profileImage Profile image file
     * @return The updated SpecialistDTO object
     * @throws IOException If an error occurs during image upload
     */
    private SpecialistResponseDTO updateSpecialist(Specialist specialist, SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage) throws IOException {
        // Handle profile image upload
        handleProfileImageUpload(profileImage, specialist);

        // Update specialist fields
        if (specialistUpdateRequestDTO.getFirstName() != null) {
            specialist.setFirstName(specialistUpdateRequestDTO.getFirstName());
        }
        if (specialistUpdateRequestDTO.getLastName() != null) {
            specialist.setLastName(specialistUpdateRequestDTO.getLastName());
        }
        if (specialistUpdateRequestDTO.getMobileNumber() != null) {
            specialist.setMobileNumber(specialistUpdateRequestDTO.getMobileNumber());
        }
        if (specialistUpdateRequestDTO.getSpeciality() != null) {
            specialist.setSpeciality(specialistUpdateRequestDTO.getSpeciality());
        }
        if (specialistUpdateRequestDTO.getAbout() != null) {
            specialist.setAbout(specialistUpdateRequestDTO.getAbout());
        }
        if (specialistUpdateRequestDTO.getPassword() != null) {
            specialist.setPassword(passwordEncoder.encode(specialistUpdateRequestDTO.getPassword()));
        }

        // Update address fields separately
        updateAddressDetails(specialist, specialistUpdateRequestDTO);

        specialistRepository.save(specialist);
        logger.info("Updated specialist profile with ID: {}", specialist.getSpecialistId());
        return specialistMapper.toDTO(specialist);
    }


    /**
     * Updates the address details for a specialist.
     *
     * @param specialist The specialist whose address is to be updated
     * @param specialistUpdateRequestDTO DTO containing the address information
     */
    private void updateAddressDetails(Specialist specialist, SpecialistUpdateRequestDTO specialistUpdateRequestDTO) {
        if (specialistUpdateRequestDTO.getPincode() != null) {
            specialist.getAddress().setPincode(specialistUpdateRequestDTO.getPincode());
        }
        if (specialistUpdateRequestDTO.getCity() != null) {
            specialist.getAddress().setCity(specialistUpdateRequestDTO.getCity());
        }
        if (specialistUpdateRequestDTO.getState() != null) {
            specialist.getAddress().setState(specialistUpdateRequestDTO.getState());
        }
        if (specialistUpdateRequestDTO.getCountry() != null) {
            specialist.getAddress().setCountry(specialistUpdateRequestDTO.getCountry());
        }
        if (specialistUpdateRequestDTO.getLocality() != null) {
            specialist.getAddress().setLocality(specialistUpdateRequestDTO.getLocality());
        }
    }


    /**
     * Deletes a specialist by admin.
     *
     * @param id The ID of the specialist to delete
     * @throws IOException If an error occurs during image deletion
     */
    @Transactional
    public void deleteSpecialistByAdmin(Long id) throws IOException {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));

        if (specialist.getAvatarPublicId() != null) {
            uploadService.deleteImage(specialist.getAvatarPublicId());
        }

        specialistRepository.delete(specialist);
        logger.info("Deleted specialist with ID: {}", id);
    }

    /**
     * Searches for specialists based on a keyword.
     *
     * @param keyword  The keyword to search for
     * @param pageable Pageable object for pagination
     * @return A page of specialists matching the keyword
     */
    public Page<SpecialistResponseDTO> searchSpecialists(String keyword, Pageable pageable) {
        Page<Specialist> specialists = specialistRepository.findByFirstNameContainingOrSpecialityContaining(keyword, keyword, pageable);
        logger.info("Searched specialists with keyword: {}", keyword);
        return specialists.map(specialistMapper::toDTO);
    }


    /**
     * Handles the upload of a profile image for a specialist.
     *
     * @param profileImage The profile image file
     * @param specialist   The specialist whose profile image is to be uploaded
     * @throws IOException If an error occurs during image upload
     */
    private void handleProfileImageUpload(MultipartFile profileImage, Specialist specialist) throws IOException {
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = uploadProfileImage(profileImage);
            specialist.setAvatarUrl(imageInfo.get("avatarUrl"));
            specialist.setAvatarPublicId(imageInfo.get("avatarPublicId"));
            logger.info("Uploaded profile image for specialist with ID: {}", specialist.getSpecialistId());
        }
    }

    /**
     * Uploads a profile image for a specialist.
     *
     * @param profileImage The profile image file
     * @return A map containing the URL and public ID of the uploaded image
     * @throws IOException If an error occurs during image upload
     */
    private Map<String, String> uploadProfileImage(MultipartFile profileImage) throws IOException {
        Map<String, Object> uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.SPECIALIST);
        logger.info("Profile image uploaded with public ID: {}", uploadResult.get("public_id"));
        return Map.of(
                "avatarUrl", (String) uploadResult.get("url"),
                "avatarPublicId", (String) uploadResult.get("public_id")
        );
    }
}