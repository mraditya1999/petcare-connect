package com.petconnect.backend.services;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.utils.TempUserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempUserStore tempUserStore;
    private final SpecialistMapper specialistMapper;
    private final UploadService uploadService;
    private final VerificationService verificationService;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, RoleRepository roleRepository,
                             AddressRepository addressRepository, @Lazy PasswordEncoder passwordEncoder, TempUserStore tempUserStore,
                             SpecialistMapper specialistMapper, UploadService uploadService, @Lazy VerificationService verificationService) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.tempUserStore = tempUserStore;
        this.specialistMapper = specialistMapper;
        this.uploadService = uploadService;
        this.verificationService = verificationService;
    }

    @Transactional
    public SpecialistDTO createSpecialist(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) throws IOException {
        if (userRepository.existsByEmail(specialistCreateRequestDTO.getEmail())) {
            logger.warn("Specialist already exists with email: {}", specialistCreateRequestDTO.getEmail());
            throw new UserAlreadyExistsException("Specialist already exists with this email.");
        }

        Specialist specialist = specialistMapper.toSpecialistEntity(specialistCreateRequestDTO);
        specialist.setPassword(passwordEncoder.encode(specialistCreateRequestDTO.getPassword()));
        specialist.setVerificationToken(UUID.randomUUID().toString());
        specialist.setVerified(false);

        // Assign roles
        assignRolesToSpecialist(specialist);

        // Handle profile image upload if it exists
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = handleProfileImageUpload(profileImage);
            specialist.setAvatarUrl(imageInfo.get("avatarUrl"));
            specialist.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }

        // Save the specialist temporarily
        tempUserStore.saveTemporaryUser(specialist.getVerificationToken(), specialist);
        verificationService.sendVerificationEmail(specialist);
        logger.info("Specialist registered with email: {}", specialist.getEmail());

        return specialistMapper.toDTO(specialist);
    }

    private void assignRolesToSpecialist(Specialist specialist) {
        Role role = roleRepository.findByRoleName(Role.RoleName.SPECIALIST)
                .orElseThrow(() -> new RuntimeException("SPECIALIST role not found"));
        specialist.setRoles(Set.of(role));
    }

    public Map<String, String> handleProfileImageUpload(MultipartFile profileImage) throws IOException {
        Map<String, Object> uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.SPECIALIST);

        String avatarUrl = (String) uploadResult.get("url");
        String avatarPublicId = (String) uploadResult.get("public_id");

        Map<String, String> imageInfo = new HashMap<>();
        imageInfo.put("avatarUrl", avatarUrl);
        imageInfo.put("avatarPublicId", avatarPublicId);
        return imageInfo;
    }

    @Transactional
    public SpecialistDTO updateSpecialist(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage, UserDetails userDetails) throws IOException {
        Specialist specialist = (Specialist) userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + userDetails.getUsername()));

        // Handle profile image upload
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = handleProfileImageUpload(profileImage);
            specialist.setAvatarUrl(imageInfo.get("avatarUrl"));
            specialist.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }

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

        // Update address fields if provided
        if (specialistUpdateRequestDTO.getAddressDTO() != null) {
            AddressDTO addressDTO = specialistUpdateRequestDTO.getAddressDTO();
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

            // Save the address
            addressRepository.save(address);
        }

        specialistRepository.save(specialist);
        return specialistMapper.toDTO(specialist);
    }

    public List<SpecialistDTO> getAllSpecialists() {
        return specialistRepository.findAll().stream()
                .map(specialistMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SpecialistDTO getSpecialistById(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
        return specialistMapper.toDTO(specialist);
    }
}
