////package com.petconnect.backend.services;
////
////import com.petconnect.backend.dto.AddressDTO;
////import com.petconnect.backend.dto.SpecialistCreateRequestDTO;
////import com.petconnect.backend.dto.SpecialistDTO;
////import com.petconnect.backend.dto.SpecialistUpdateRequestDTO;
////import com.petconnect.backend.entity.Address;
////import com.petconnect.backend.entity.Role;
////import com.petconnect.backend.entity.Specialist;
////import com.petconnect.backend.entity.User;
////import com.petconnect.backend.exceptions.ImageDeletionException;
////import com.petconnect.backend.exceptions.ImageUploadException;
////import com.petconnect.backend.exceptions.ResourceNotFoundException;
////import com.petconnect.backend.exceptions.UserAlreadyExistsException;
////import com.petconnect.backend.mappers.SpecialistMapper;
////import com.petconnect.backend.repositories.AddressRepository;
////import com.petconnect.backend.repositories.RoleRepository;
////import com.petconnect.backend.repositories.SpecialistRepository;
////import com.petconnect.backend.repositories.UserRepository;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.core.userdetails.UserDetails;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////import org.springframework.web.multipart.MultipartFile;
////
////import java.io.IOException;
////import java.util.List;
////import java.util.Map;
////import java.util.Optional;
////import java.util.Set;
////import java.util.stream.Collectors;
////
////@Service
////public class SpecialistService {
////
////    private static final Logger log = LoggerFactory.getLogger(SpecialistService.class);
////
////    private final SpecialistRepository specialistRepository;
////    private final UserRepository userRepository;
////    private final SpecialistMapper specialistMapper;
////    private final PasswordEncoder passwordEncoder;
////    private final UploadService uploadService;
////    private final AddressRepository addressRepository;
////    private final RoleRepository roleRepository;
////
////    @Autowired
////    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, SpecialistMapper specialistMapper, PasswordEncoder passwordEncoder, UploadService uploadService, AddressRepository addressRepository, RoleRepository roleRepository) {
////        this.specialistRepository = specialistRepository;
////        this.userRepository = userRepository;
////        this.specialistMapper = specialistMapper;
////        this.passwordEncoder = passwordEncoder;
////        this.uploadService = uploadService;
////        this.addressRepository = addressRepository;
////        this.roleRepository = roleRepository;
////    }
////
////    @Transactional
////    public SpecialistDTO createSpecialist(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) {
////        Optional<User> existingUser = userRepository.findByEmail(specialistCreateRequestDTO.getEmail());
////        if (existingUser.isPresent() && existingUser.get().isVerified()) {
////            throw new UserAlreadyExistsException("User with this email already exists and is verified.");
////        } else if (existingUser.isPresent() && !existingUser.get().isVerified()) {
////            throw new UserAlreadyExistsException("User with this email already exists but not verified.");
////        }
////
////        Specialist specialist = specialistMapper.toSpecialistEntity(specialistCreateRequestDTO);
////        specialist.setPassword(passwordEncoder.encode(specialistCreateRequestDTO.getPassword()));
////
////        Address address = specialist.getAddress();
////        if (address != null) {
////            address = addressRepository.save(address);
////            specialist.setAddress(address);
////        }
////
////        try {
////            handleImageUpload(specialist, profileImage);
////        } catch (IOException | ImageUploadException e) {
////            log.error("Error handling image upload", e);
////            throw new ImageUploadException("Error handling profile image", e);
////        }
////
////        Optional<Role> specialistRole = roleRepository.findByRoleName(Role.RoleName.SPECIALIST);
////        if (specialistRole.isPresent()) {
////            specialist.setRoles(Set.of(specialistRole.get()));
////        } else {
////            log.error("SPECIALIST role not found!");
////            throw new RuntimeException("SPECIALIST role not found!"); // Or custom exception
////        }
////
////        Specialist savedSpecialist = specialistRepository.save(specialist);
////        return specialistMapper.toDTO(savedSpecialist);
////    }
////
////    private void handleImageUpload(Specialist specialist, MultipartFile profileImage) throws IOException {
////        if (profileImage != null && !profileImage.isEmpty()) {
////            Map<String, Object> uploadResult;
////            if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
////                uploadResult = uploadService.updateImage(specialist.getAvatarPublicId(), profileImage, UploadService.ProfileType.SPECIALIST);
////            } else {
////                uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.SPECIALIST);
////            }
////            specialist.setAvatarUrl((String) uploadResult.get("url"));
////            specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
////        }
////    }
////
////    public List<SpecialistDTO> getAllSpecialists() {
////        List<Specialist> specialists = specialistRepository.findAll();
////        return specialists.stream()
////                .map(specialistMapper::toDTO)
////                .collect(Collectors.toList());
////    }
////
////    public SpecialistDTO getSpecialistById(Long id) {
////        Specialist specialist = specialistRepository.findById(id)
////                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
////        return specialistMapper.toDTO(specialist);
////    }
////
////    @Transactional
////    public SpecialistDTO updateSpecialist(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage, UserDetails userDetails) {
////        // Retrieve the current user's username from UserDetails
////        String currentUsername = userDetails.getUsername();
////
////        // Use the UserRepository to find the User by the username
////        User user = userRepository.findByEmail(currentUsername)
////                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUsername));
////
////        // Check if the user is a Specialist
////        if (!(user instanceof Specialist)) {
////            throw new ResourceNotFoundException("User is not a specialist");
////        }
////
////        Specialist specialist = (Specialist) user;
////
////        // Update only the provided fields
////        if (specialistUpdateRequestDTO.getFirstName() != null) {
////            specialist.setFirstName(specialistUpdateRequestDTO.getFirstName());
////        }
////        if (specialistUpdateRequestDTO.getLastName() != null) {
////            specialist.setLastName(specialistUpdateRequestDTO.getLastName());
////        }
////        if (specialistUpdateRequestDTO.getEmail() != null) {
////            specialist.setEmail(specialistUpdateRequestDTO.getEmail());
////        }
////        if (specialistUpdateRequestDTO.getMobileNumber() != null) {
////            specialist.setMobileNumber(specialistUpdateRequestDTO.getMobileNumber());
////        }
////        if (specialistUpdateRequestDTO.getSpeciality() != null) {
////            specialist.setSpeciality(specialistUpdateRequestDTO.getSpeciality());
////        }
////        if (specialistUpdateRequestDTO.getAbout() != null) {
////            specialist.setAbout(specialistUpdateRequestDTO.getAbout());
////        }
////        if (specialistUpdateRequestDTO.getPassword() != null) {
////            specialist.setPassword(passwordEncoder.encode(specialistUpdateRequestDTO.getPassword())); // Ensure the password is encoded
////        }
////
////        // Update address fields if provided
////        Address address = specialist.getAddress();
////        if (specialistUpdateRequestDTO.getAddressDTO() != null) {
////            AddressDTO addressDTO = specialistUpdateRequestDTO.getAddressDTO();
////
////            if (address == null) {
////                address = new Address();
////                specialist.setAddress(address);
////            }
////
////            if (addressDTO.getPincode() != null) {
////                address.setPincode(addressDTO.getPincode());
////            }
////            if (addressDTO.getCity() != null) {
////                address.setCity(addressDTO.getCity());
////            }
////            if (addressDTO.getState() != null) {
////                address.setState(addressDTO.getState());
////            }
////            if (addressDTO.getLocality() != null) {
////                address.setLocality(addressDTO.getLocality());
////            }
////            if (addressDTO.getCountry() != null) {
////                address.setCountry(addressDTO.getCountry());
////            }
////
////            addressRepository.save(address); // Save the address
////        }
////
////        try {
////            handleImageUpload(specialist, profileImage);
////        } catch (IOException | ImageUploadException e) {
////            log.error("Error handling image upload", e);
////            throw new ImageUploadException("Error handling profile image", e);
////        }
////
////        Specialist updatedSpecialist = specialistRepository.save(specialist);
////        return specialistMapper.toDTO(updatedSpecialist);
////    }
////
////    @Transactional
////    public void deleteCurrentSpecialist(UserDetails userDetails) {
////        // Retrieve the current user's username from UserDetails
////        String currentUsername = userDetails.getUsername();
////
////        // Use the UserRepository to find the User by the username
////        User user = userRepository.findByEmail(currentUsername)
////                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUsername));
////
////        // Check if the user is a Specialist
////        if (!(user instanceof Specialist)) {
////            throw new ResourceNotFoundException("User is not a specialist");
////        }
////
////        Specialist specialist = (Specialist) user;
////
////        // Delete associated image from Cloudinary if it exists
////        if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
////            try {
////                uploadService.deleteImage(specialist.getAvatarPublicId());
////            } catch (IOException e) {
////                log.error("Error deleting profile image from Cloudinary", e);
////                throw new ImageDeletionException("Error deleting profile image", e);
////            }
////        }
////
////        specialistRepository.delete(specialist); // Use specialistRepository.delete(specialist)
////    }
////}
//
//
//package com.petconnect.backend.services;
//
//import com.petconnect.backend.dto.*;
//import com.petconnect.backend.entity.Address;
//import com.petconnect.backend.entity.Role;
//import com.petconnect.backend.entity.Specialist;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.exceptions.*;
//import com.petconnect.backend.mappers.SpecialistMapper;
//import com.petconnect.backend.repositories.*;
//import com.petconnect.backend.services.UploadService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class SpecialistService {
//
//    private static final Logger log = LoggerFactory.getLogger(SpecialistService.class);
//
//    private final SpecialistRepository specialistRepository;
//    private final UserRepository userRepository;
//    private final SpecialistMapper specialistMapper;
//    private final PasswordEncoder passwordEncoder;
//    private final UploadService uploadService;
//    private final AddressRepository addressRepository;
//    private final RoleRepository roleRepository;
//
//    @Autowired
//    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, SpecialistMapper specialistMapper, PasswordEncoder passwordEncoder, UploadService uploadService, AddressRepository addressRepository, RoleRepository roleRepository) {
//        this.specialistRepository = specialistRepository;
//        this.userRepository = userRepository;
//        this.specialistMapper = specialistMapper;
//        this.passwordEncoder = passwordEncoder;
//        this.uploadService = uploadService;
//        this.addressRepository = addressRepository;
//        this.roleRepository = roleRepository;
//    }
//
//    @Transactional
//    public SpecialistDTO createSpecialist(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) {
//        Optional<User> existingUser = userRepository.findByEmail(specialistCreateRequestDTO.getEmail());
//        if (existingUser.isPresent() && existingUser.get().isVerified()) {
//            throw new UserAlreadyExistsException("User with this email already exists and is verified.");
//        } else if (existingUser.isPresent() && !existingUser.get().isVerified()) {
//            throw new UserAlreadyExistsException("User with this email already exists but not verified.");
//        }
//
//        Specialist specialist = specialistMapper.toSpecialistEntity(specialistCreateRequestDTO);
//        specialist.setPassword(passwordEncoder.encode(specialistCreateRequestDTO.getPassword()));
//
//        Address address = specialist.getAddress();
//        if (address != null) {
//            address = addressRepository.save(address);
//            specialist.setAddress(address);
//        }
//
//        try {
//            handleImageUpload(specialist, profileImage);
//        } catch (IOException | ImageUploadException e) {
//            log.error("Error handling image upload", e);
//            throw new ImageUploadException("Error handling profile image", e);
//        }
//
//        Optional<Role> specialistRole = roleRepository.findByRoleName(Role.RoleName.SPECIALIST);
//        if (specialistRole.isPresent()) {
//            specialist.setRoles(Set.of(specialistRole.get()));
//        } else {
//            log.error("SPECIALIST role not found!");
//            throw new RuntimeException("SPECIALIST role not found!"); // Or custom exception
//        }
//
//        Specialist savedSpecialist = specialistRepository.save(specialist);
//        return specialistMapper.toDTO(savedSpecialist);
//    }
//
//    private void handleImageUpload(Specialist specialist, MultipartFile profileImage) throws IOException {
//        if (profileImage != null && !profileImage.isEmpty()) {
//            // Delete old image first if it exists
//            if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
//                log.info("Deleting old profile image for specialist with ID: {}", specialist.getSpecialistId());
//                try {
//                    uploadService.deleteImage(specialist.getAvatarPublicId());
//                } catch (IOException e) {
//                    log.error("Error deleting old avatar with ID: {}", specialist.getAvatarPublicId(), e);
//                    throw new ImageDeletionException("Error deleting old avatar image", e);
//                }
//            }
//
//            // Upload new image
//            log.info("Uploading new profile image for specialist");
//            Map<String, Object> uploadResult = uploadService.uploadImage(profileImage, UploadService.ProfileType.SPECIALIST);
//            specialist.setAvatarUrl((String) uploadResult.get("url"));
//            specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
//        }
//    }
//
//    public List<SpecialistDTO> getAllSpecialists() {
//        List<Specialist> specialists = specialistRepository.findAll();
//        return specialists.stream()
//                .map(specialistMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public SpecialistDTO getSpecialistById(Long id) {
//        Specialist specialist = specialistRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
//        return specialistMapper.toDTO(specialist);
//    }
//
//    @Transactional
//    public SpecialistDTO updateSpecialist(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage, UserDetails userDetails) {
//        // Retrieve the current user's username from UserDetails
//        String currentUsername = userDetails.getUsername();
//
//        // Use the UserRepository to find the User by the username
//        User user = userRepository.findByEmail(currentUsername)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUsername));
//
//        // Check if the user is a Specialist
//        if (!(user instanceof Specialist)) {
//            throw new ResourceNotFoundException("User is not a specialist");
//        }
//
//        Specialist specialist = (Specialist) user;
//
//        // Update only the provided fields
//        if (specialistUpdateRequestDTO.getFirstName() != null) {
//            specialist.setFirstName(specialistUpdateRequestDTO.getFirstName());
//        }
//        if (specialistUpdateRequestDTO.getLastName() != null) {
//            specialist.setLastName(specialistUpdateRequestDTO.getLastName());
//        }
//        if (specialistUpdateRequestDTO.getEmail() != null) {
//            specialist.setEmail(specialistUpdateRequestDTO.getEmail());
//        }
//        if (specialistUpdateRequestDTO.getMobileNumber() != null) {
//            specialist.setMobileNumber(specialistUpdateRequestDTO.getMobileNumber());
//        }
//        if (specialistUpdateRequestDTO.getSpeciality() != null) {
//            specialist.setSpeciality(specialistUpdateRequestDTO.getSpeciality());
//        }
//        if (specialistUpdateRequestDTO.getAbout() != null) {
//            specialist.setAbout(specialistUpdateRequestDTO.getAbout());
//        }
//        if (specialistUpdateRequestDTO.getPassword() != null) {
//            specialist.setPassword(passwordEncoder.encode(specialistUpdateRequestDTO.getPassword())); // Ensure the password is encoded
//        }
//
//        // Update address fields if provided
//        Address address = specialist.getAddress();
//        if (specialistUpdateRequestDTO.getAddressDTO() != null) {
//            AddressDTO addressDTO = specialistUpdateRequestDTO.getAddressDTO();
//
//            if (address == null) {
//                address = new Address();
//                specialist.setAddress(address);
//            }
//
//            if (addressDTO.getPincode() != null) {
//                address.setPincode(addressDTO.getPincode());
//            }
//            if (addressDTO.getCity() != null) {
//                address.setCity(addressDTO.getCity());
//            }
//            if (addressDTO.getState() != null) {
//                address.setState(addressDTO.getState());
//            }
//            if (addressDTO.getLocality() != null) {
//                address.setLocality(addressDTO.getLocality());
//            }
//            if (addressDTO.getCountry() != null) {
//                address.setCountry(addressDTO.getCountry());
//            }
//
//            addressRepository.save(address); // Save the address
//        }
//
//        try {
//            handleImageUpload(specialist, profileImage);
//        } catch (IOException | ImageUploadException e) {
//            log.error("Error handling image upload", e);
//            throw new ImageUploadException("Error handling profile image", e);
//        }
//
//        Specialist updatedSpecialist = specialistRepository.save(specialist);
//        return specialistMapper.toDTO(updatedSpecialist);
//    }
//
//    @Transactional
//    public void deleteCurrentSpecialist(UserDetails userDetails) {
//        // Retrieve the current user's username from UserDetails
//        String currentUsername = userDetails.getUsername();
//
//        // Use the UserRepository to find the User by the username
//        User user = userRepository.findByEmail(currentUsername)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + currentUsername));
//
//        // Check if the user is a Specialist
//        if (!(user instanceof Specialist)) {
//            throw new ResourceNotFoundException("User is not a specialist");
//        }
//
//        Specialist specialist = (Specialist) user;
//
//        // Delete associated image from Cloudinary if it exists
//        if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
//            try {
//                uploadService.deleteImage(specialist.getAvatarPublicId());
//            } catch (IOException e) {
//                log.error("Error deleting profile image from Cloudinary", e);
//                throw new ImageDeletionException("Error deleting profile image", e);
//            }
//        }
//
//        specialistRepository.delete(specialist); // Use specialistRepository.delete(specialist)
//    }
//}
//############################################################################################################
//package com.petconnect.backend.services;
//
//import com.petconnect.backend.dto.SpecialistCreateRequestDTO;
//import com.petconnect.backend.dto.SpecialistDTO;
//import com.petconnect.backend.dto.SpecialistUpdateRequestDTO;
//import com.petconnect.backend.entity.Specialist;
//import com.petconnect.backend.entity.Role;
//import com.petconnect.backend.entity.User;
//import com.petconnect.backend.exceptions.ImageDeletionException;
//import com.petconnect.backend.exceptions.ResourceNotFoundException;
//import com.petconnect.backend.exceptions.UserAlreadyExistsException;
//import com.petconnect.backend.mappers.SpecialistMapper;
//import com.petconnect.backend.repositories.SpecialistRepository;
//import com.petconnect.backend.repositories.RoleRepository;
//import com.petconnect.backend.repositories.UserRepository;
//import com.petconnect.backend.utils.TempUserStore;
//import com.petconnect.backend.services.EmailService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class SpecialistService {
//
//    private static final Logger logger = LoggerFactory.getLogger(SpecialistService.class);
//
//    private final SpecialistRepository specialistRepository;
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final TempUserStore tempUserStore;
//    private final EmailService emailService;
//    private final SpecialistMapper specialistMapper;
//    private final UploadService uploadService;
//
//    @Autowired
//    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, RoleRepository roleRepository,
//                             @Lazy PasswordEncoder passwordEncoder, TempUserStore tempUserStore, EmailService emailService,
//                             SpecialistMapper specialistMapper, UploadService uploadService) {
//        this.specialistRepository = specialistRepository;
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.tempUserStore = tempUserStore;
//        this.emailService = emailService;
//        this.specialistMapper = specialistMapper;
//        this.uploadService = uploadService;
//    }
//
//    @Transactional
//    public SpecialistDTO createSpecialist(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) throws IOException {
//        if (userRepository.existsByEmail(specialistCreateRequestDTO.getEmail())) {
//            logger.warn("Specialist already exists with email: {}", specialistCreateRequestDTO.getEmail());
//            throw new UserAlreadyExistsException("Specialist already exists with this email.");
//        }
//
//        Specialist specialist = specialistMapper.toSpecialistEntity(specialistCreateRequestDTO);
//        specialist.setPassword(passwordEncoder.encode(specialistCreateRequestDTO.getPassword()));
//        specialist.setVerificationToken(UUID.randomUUID().toString());
//        specialist.setVerified(false);
//
//        // Assign roles
//        assignRolesToSpecialist(specialist);
//
//        // Save the specialist temporarily
//        tempUserStore.saveTemporaryUser(specialist.getVerificationToken(), specialist);
//        emailService.sendVerificationEmail(specialist);
//        logger.info("Specialist registered with email: {}", specialist.getEmail());
//
//        return specialistMapper.toDTO(specialist);
//    }
//
//    private void assignRolesToSpecialist(Specialist specialist) {
//        Role role = roleRepository.findByRoleName(Role.RoleName.SPECIALIST)
//                .orElseThrow(() -> new RuntimeException("SPECIALIST role not found"));
//        specialist.setRoles(Set.of(role));
//    }
//
//    @Transactional
//    public boolean verifySpecialist(String verificationToken) {
//        Specialist tempSpecialist = (Specialist) tempUserStore.getTemporaryUser(verificationToken);
//        if (tempSpecialist != null) {
//            logger.info("Verifying specialist with token: {}", verificationToken);
//
//            tempSpecialist.setVerified(true);
//            tempSpecialist.setVerificationToken(null);
//
//            specialistRepository.save(tempSpecialist);
//
//            Specialist verifiedSpecialist = specialistRepository.findById(tempSpecialist.getSpecialistId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Specialist not found"));
//            logger.info("After saving: isVerified={}", verifiedSpecialist.isVerified());
//
//            return true;
//        }
//        logger.warn("Verification token invalid or expired: {}", verificationToken);
//        return false;
//    }
//
//    @Transactional
//    public boolean resetSpecialistPassword(String resetToken, String newPassword) {
//        Specialist specialist = (Specialist) userRepository.findByResetToken(resetToken)
//                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token."));
//
//        specialist.setPassword(passwordEncoder.encode(newPassword));
//        specialist.setResetToken(null);
//        userRepository.save(specialist);
//        logger.info("Password reset for specialist with email: {}", specialist.getEmail());
//        return true;
//    }
//
//    public SpecialistDTO updateSpecialist(SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage, UserDetails userDetails) {
//        Specialist specialist = (Specialist) userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + userDetails.getUsername()));
//
//        specialistMapper.updateSpecialistEntity(specialistUpdateRequestDTO, specialist);
//
//        // Handle profile image upload
//        if (profileImage != null && !profileImage.isEmpty()) {
//            // Implement profile image handling logic here...
//        }
//
//        specialistRepository.save(specialist);
//        return specialistMapper.toDTO(specialist);
//    }
//
//    public List<SpecialistDTO> getAllSpecialists() {
//        return specialistRepository.findAll().stream()
//                .map(specialistMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public SpecialistDTO getSpecialistById(Long id) {
//        Specialist specialist = specialistRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
//        return specialistMapper.toDTO(specialist);
//    }
//
//    @Transactional
//    public void deleteCurrentSpecialist(UserDetails userDetails) {
//        Specialist specialist = (Specialist) userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + userDetails.getUsername()));
//
//        // Check and delete the specialist's avatar if it exists
//        if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
//            try {
//                uploadService.deleteImage(specialist.getAvatarPublicId());
//            } catch (IOException e) {
//                logger.error("Error deleting avatar with ID: {}", specialist.getAvatarPublicId(), e);
//                throw new ImageDeletionException("Error deleting avatar image", e);
//            }
//        }
//
//        specialistRepository.delete(specialist);
//        logger.info("Specialist deleted with email: {}", specialist.getEmail());
//    }
//}

package com.petconnect.backend.services;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.SpecialistCreateRequestDTO;
import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.dto.SpecialistUpdateRequestDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.exceptions.ImageDeletionException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.utils.TempUserStore;
import com.petconnect.backend.services.EmailService;
import com.petconnect.backend.services.UploadService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialistService.class);

    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TempUserStore tempUserStore;
    private final EmailService emailService;
    private final SpecialistMapper specialistMapper;
    private final UploadService uploadService;
    private final AddressRepository addressRepository;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, RoleRepository roleRepository,
                             @Lazy PasswordEncoder passwordEncoder, TempUserStore tempUserStore, EmailService emailService,
                             SpecialistMapper specialistMapper, UploadService uploadService, AddressRepository addressRepository) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tempUserStore = tempUserStore;
        this.emailService = emailService;
        this.specialistMapper = specialistMapper;
        this.uploadService = uploadService;
        this.addressRepository = addressRepository;
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

        // Save the specialist temporarily
        tempUserStore.saveTemporaryUser(specialist.getVerificationToken(), specialist);
        emailService.sendVerificationEmail(specialist);
        logger.info("Specialist registered with email: {}", specialist.getEmail());

        // Handle profile image upload if it exists
        if (profileImage != null && !profileImage.isEmpty()) {
            Map<String, String> imageInfo = handleProfileImageUpload(profileImage);
            specialist.setAvatarUrl(imageInfo.get("avatarUrl"));
            specialist.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }

        // Save the specialist
        specialistRepository.save(specialist);
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
    public boolean verifySpecialist(String verificationToken) {
        Specialist tempSpecialist = (Specialist) tempUserStore.getTemporaryUser(verificationToken);
        if (tempSpecialist != null) {
            logger.info("Verifying specialist with token: {}", verificationToken);

            tempSpecialist.setVerified(true);
            tempSpecialist.setVerificationToken(null);

            specialistRepository.save(tempSpecialist);

            Specialist verifiedSpecialist = specialistRepository.findById(tempSpecialist.getSpecialistId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialist not found"));
            logger.info("After saving: isVerified={}", verifiedSpecialist.isVerified());

            return true;
        }
        logger.warn("Verification token invalid or expired: {}", verificationToken);
        return false;
    }

    @Transactional
    public boolean resetSpecialistPassword(String resetToken, String newPassword) {
        Specialist specialist = (Specialist) userRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token."));

        specialist.setPassword(passwordEncoder.encode(newPassword));
        specialist.setResetToken(null);
        userRepository.save(specialist);
        logger.info("Password reset for specialist with email: {}", specialist.getEmail());
        return true;
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
            specialist.setAddress(address);
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

    @Transactional
    public void deleteCurrentSpecialist(UserDetails userDetails) {
        Specialist specialist = (Specialist) userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + userDetails.getUsername()));

        // Check and delete the specialist's avatar if it exists
        if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
            try {
                uploadService.deleteImage(specialist.getAvatarPublicId());
            } catch (IOException e) {
                logger.error("Error deleting avatar with ID: {}", specialist.getAvatarPublicId(), e);
                throw new ImageDeletionException("Error deleting avatar image", e);
            }
        }

        specialistRepository.delete(specialist);
        logger.info("Specialist deleted with email: {}", specialist.getEmail());
    }
}
