package com.petconnect.backend.services;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.SpecialistCreateRequestDTO;
import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.dto.SpecialistUpdateRequestDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ImageUploadException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    private static final Logger log = LoggerFactory.getLogger(SpecialistService.class);

    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final SpecialistMapper specialistMapper;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, SpecialistMapper specialistMapper, PasswordEncoder passwordEncoder, UploadService uploadService, AddressRepository addressRepository, RoleRepository roleRepository) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.specialistMapper = specialistMapper;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
        this.addressRepository = addressRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public SpecialistDTO createSpecialist(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) {
        Optional<User> existingUser = userRepository.findByEmail(specialistCreateRequestDTO.getEmail());
        if (existingUser.isPresent() && existingUser.get().isVerified()) {
            throw new UserAlreadyExistsException("User with this email already exists and is verified.");
        } else if (existingUser.isPresent() && !existingUser.get().isVerified()) {
            throw new UserAlreadyExistsException("User with this email already exists but not verified.");
        }

        Specialist specialist = specialistMapper.toSpecialistEntity(specialistCreateRequestDTO);
        specialist.setPassword(passwordEncoder.encode(specialistCreateRequestDTO.getPassword()));

        Address address = specialist.getAddress();
        if (address != null) {
            address = addressRepository.save(address);
            specialist.setAddress(address);
        }

        try {
            handleImageUpload(specialist, profileImage);
        } catch (IOException | ImageUploadException e) {
            log.error("Error handling image upload", e);
            throw new ImageUploadException("Error handling profile image", e);
        }

        Optional<Role> specialistRole = roleRepository.findByRoleName(Role.RoleName.SPECIALIST);
        if (specialistRole.isPresent()) {
            specialist.setRoles(Set.of(specialistRole.get()));
        } else {
            log.error("SPECIALIST role not found!");
            throw new RuntimeException("SPECIALIST role not found!"); // Or custom exception
        }

        Specialist savedSpecialist = specialistRepository.save(specialist);
        return specialistMapper.toDTO(savedSpecialist);
    }

    private void handleImageUpload(Specialist specialist, MultipartFile profileImage) throws IOException {
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, Object> uploadResult;
            if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
                uploadResult = uploadService.updateImage(specialist.getAvatarPublicId(), profileImage);
            } else {
                uploadResult = uploadService.uploadImage(profileImage);
            }
            specialist.setAvatarUrl((String) uploadResult.get("url"));
            specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
        }
    }

    public List<SpecialistDTO> getAllSpecialists() {
        List<Specialist> specialists = specialistRepository.findAll();
        return specialists.stream()
                .map(specialistMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SpecialistDTO getSpecialistById(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
        return specialistMapper.toDTO(specialist);
    }

    @Transactional
    public SpecialistDTO updateSpecialist(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage, UserDetails userDetails) {
        // Retrieve the current user's username from UserDetails
        String currentUsername = userDetails.getUsername();

        // Use the UserRepository to find the User by the username
        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUsername));

        // Check if the user is a Specialist
        if (!(user instanceof Specialist)) {
            throw new ResourceNotFoundException("User is not a specialist");
        }

        Specialist specialist = (Specialist) user;

        // Update only the provided fields
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
            specialist.setPassword(passwordEncoder.encode(specialistUpdateRequestDTO.getPassword())); // Ensure the password is encoded
        }

        // Update address fields if provided
        Address address = specialist.getAddress();
        if (specialistUpdateRequestDTO.getAddressDTO() != null) {
            AddressDTO addressDTO = specialistUpdateRequestDTO.getAddressDTO();

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

            addressRepository.save(address); // Save the address
        }

        try {
            handleImageUpload(specialist, profileImage);
        } catch (IOException | ImageUploadException e) {
            log.error("Error handling image upload", e);
            throw new ImageUploadException("Error handling profile image", e);
        }

        Specialist updatedSpecialist = specialistRepository.save(specialist);
        return specialistMapper.toDTO(updatedSpecialist);
    }

    @Transactional
    public void deleteCurrentSpecialist(UserDetails userDetails) {
        // Retrieve the current user's username from UserDetails
        String currentUsername = userDetails.getUsername();

        // Use the UserRepository to find the User by the username
        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUsername));

        // Check if the user is a Specialist
        if (!(user instanceof Specialist)) {
            throw new ResourceNotFoundException("User is not a specialist");
        }

        Specialist specialist = (Specialist) user;

        specialistRepository.delete(specialist); // Use specialistRepository.delete(specialist)
    }
}
