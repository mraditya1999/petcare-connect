package com.petconnect.backend.services;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.utils.RoleAssignmentUtil;
import com.petconnect.backend.utils.TempUserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialistService.class);

    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final RoleAssignmentUtil roleAssignmentUtil;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpecialistMapper specialistMapper;
    private final UploadService uploadService;
    private final VerificationService verificationService;
    private final TempUserStore tempUserStore;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, RoleAssignmentUtil roleAssignmentUtil,
                             AddressRepository addressRepository, @Lazy PasswordEncoder passwordEncoder,
                             SpecialistMapper specialistMapper, UploadService uploadService, VerificationService verificationService, TempUserStore tempUserStore) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.roleAssignmentUtil = roleAssignmentUtil;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.specialistMapper = specialistMapper;
        this.uploadService = uploadService;
        this.verificationService = verificationService;
        this.tempUserStore = tempUserStore;
    }

    public Page<SpecialistDTO> getAllSpecialists(Pageable pageable) {
        return specialistRepository.findAll(pageable).map(specialistMapper::toDTO);
    }

    public SpecialistDTO getSpecialistById(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
        return specialistMapper.toDTO(specialist);
    }

    @Transactional
    public SpecialistDTO updateSpecialist(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage, UserDetails userDetails) throws IOException {
        Specialist specialist = (Specialist) userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + userDetails.getUsername()));
        return updateSpecialist(specialist, specialistUpdateRequestDTO, profileImage);
    }

    //    ADMIN SERVICES
    @Transactional
    public SpecialistDTO createSpecialistByAdmin(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) throws IOException {
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
        tempUserStore.saveTemporaryUser(specialist.getVerificationToken(), specialist);
        verificationService.sendVerificationEmail(specialist);
        logger.info("Specialist created by admin with email: {}", specialist.getEmail());

        return specialistMapper.toDTO(specialist);
    }

    @Transactional
    public SpecialistDTO updateSpecialistByAdmin(Long id, SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage) throws IOException {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
        return updateSpecialist(specialist, specialistUpdateRequestDTO, profileImage);
    }

    private SpecialistDTO updateSpecialist(Specialist specialist, SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage) throws IOException {
        // Handle profile image upload
        handleProfileImageUpload(profileImage, specialist);

        // Update specialist fields
        if (specialistUpdateRequestDTO.getFirstName() != null) {
            specialist.setFirstName(specialistUpdateRequestDTO.getFirstName());
        }
        if (specialistUpdateRequestDTO.getLastName() != null) {
            specialist.setLastName(specialistUpdateRequestDTO.getLastName());
        }
        if (specialistUpdateRequestDTO.getEmail() != null) {
            specialist.setEmail(specialistUpdateRequestDTO.getEmail());
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
        updateAddressDetails(specialist, specialistUpdateRequestDTO.getAddressDTO());

        specialistRepository.save(specialist);
        return specialistMapper.toDTO(specialist);
    }

    private void updateAddressDetails(Specialist specialist, AddressDTO addressDTO) {
        if (addressDTO != null) {
            Address address = specialist.getAddress();
            if (address == null) {
                address = new Address();
                specialist.setAddress(address);
            }
            if (addressDTO.getPincode() != null) {
                address.setPincode(addressDTO.getPincode());
            }
            if (addressDTO.getCity() != null) {
                address.setCity(addressDTO.getCity());
            }
            if (addressDTO.getState() != null) {
                address.setState(addressDTO.getState());
            }
            if (addressDTO.getLocality() != null) {
                address.setLocality(addressDTO.getLocality());
            }
            if (addressDTO.getCountry() != null) {
                address.setCountry(addressDTO.getCountry());
            }
            addressRepository.save(address);
        }
    }

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

    public Page<SpecialistDTO> searchSpecialists(String keyword, Pageable pageable) {
        Page<Specialist> specialists = specialistRepository.findByFirstNameContainingOrSpecialityContaining(keyword, keyword, pageable);
        return specialists.map(specialistMapper::toDTO);
    }

    private void handleProfileImageUpload(MultipartFile profileImage, Specialist specialist) throws IOException {
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = uploadProfileImage(profileImage);
            specialist.setAvatarUrl(imageInfo.get("avatarUrl"));
            specialist.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }
    }

    private Map<String, String> uploadProfileImage(MultipartFile profileImage) throws IOException {
        Map<String, Object> uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.SPECIALIST);
        return Map.of(
                "avatarUrl", (String) uploadResult.get("url"),
                "avatarPublicId", (String) uploadResult.get("public_id")
        );
    }
}