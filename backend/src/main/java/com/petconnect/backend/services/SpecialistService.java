package com.petconnect.backend.services;

import com.petconnect.backend.dto.SpecialistCreateRequestDTO;
import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.dto.SpecialistUpdateRequestDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ImageUploadException;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UserAlreadyExistsException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.AddressRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//@Service
//public class SpecialistService {
//
//    private final SpecialistRepository specialistRepository;
//    private final SpecialistMapper specialistMapper;
//    private final UploadService uploadService;
//
//    @Autowired
//    public SpecialistService(SpecialistRepository specialistRepository, SpecialistMapper specialistMapper, UploadService uploadService) {
//        this.specialistRepository = specialistRepository;
//        this.specialistMapper = specialistMapper;
//        this.uploadService = uploadService;
//    }
//
//    public SpecialistDTO createSpecialist(SpecialistDTO specialistDTO, MultipartFile profileImage) throws IOException {
//        Specialist specialist = specialistMapper.toEntity(specialistDTO);
//
//        // Handle profile image upload
//        if (profileImage != null && !profileImage.isEmpty()) {
//            Map<String, Object> uploadResult = uploadService.uploadImage(profileImage);
//            specialist.setAvatarUrl((String) uploadResult.get("url"));
//            specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
//        }
//
//        Specialist savedSpecialist = specialistRepository.save(specialist);
//        return specialistMapper.toDTO(savedSpecialist);
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
//    public SpecialistDTO updateSpecialist(Long id, String firstName, String lastName, String email, String password,
//                                          String mobileNumber, String speciality, String about,
//                                          Long pincode, String city, String state, String locality,
//                                          String country, MultipartFile profileImage) throws IOException {
//        Specialist specialist = specialistRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
//
//        // Update entity fields only if provided
//        if (firstName != null) specialist.setFirstName(firstName);
//        if (lastName != null) specialist.setLastName(lastName);
//        if (email != null) specialist.setEmail(email);
//        if (password != null) specialist.setPassword(password);
//        if (mobileNumber != null) specialist.setMobileNumber(mobileNumber);
//        if (speciality != null) specialist.setSpeciality(speciality);
//        if (about != null) specialist.setAbout(about);
//
//        // Convert and update address fields
//        if (specialist.getAddress() == null) {
//            specialist.setAddress(new Address());
//        }
//
//        if (pincode != null) specialist.getAddress().setPincode(pincode);
//        if (city != null) specialist.getAddress().setCity(city);
//        if (state != null) specialist.getAddress().setState(state);
//        if (locality != null) specialist.getAddress().setLocality(locality);
//        if (country != null) specialist.getAddress().setCountry(country);
//
//        // Handle profile image upload
//        if (profileImage != null && !profileImage.isEmpty()) {
//            Map<String, Object> uploadResult;
//            if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
//                uploadResult = uploadService.updateImage(specialist.getAvatarPublicId(), profileImage);
//            } else {
//                uploadResult = uploadService.uploadImage(profileImage);
//            }
//            specialist.setAvatarUrl((String) uploadResult.get("url"));
//            specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
//        }
//
//        Specialist updatedSpecialist = specialistRepository.save(specialist);
//        return specialistMapper.toDTO(updatedSpecialist);
//    }
//
//    @Transactional
//    public void deleteSpecialist(Long id) {
//        Specialist specialist = specialistRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
//        specialistRepository.deleteById(id);
//    }
//}
//##################################################################################################
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import java.util.UUID;
//
//@Service
//public class SpecialistService {
//
//    private final SpecialistRepository specialistRepository;
//    private final SpecialistMapper specialistMapper;
//    private final JavaMailSender mailSender;
//    private final PasswordEncoder passwordEncoder;
//    private final UploadService uploadService;
//
//    @Autowired
//    public SpecialistService(SpecialistRepository specialistRepository, SpecialistMapper specialistMapper, JavaMailSender mailSender, PasswordEncoder passwordEncoder, UploadService uploadService) {
//        this.specialistRepository = specialistRepository;
//        this.specialistMapper = specialistMapper;
//        this.mailSender = mailSender;
//        this.passwordEncoder = passwordEncoder;
//        this.uploadService = uploadService;
//    }
//
//    @Transactional
//    public SpecialistDTO createSpecialist(SpecialistDTO specialistDTO, MultipartFile profileImage) throws IOException, MessagingException {
//        specialistDTO.setPassword(passwordEncoder.encode(specialistDTO.getPassword()));
//        Specialist specialist = specialistMapper.toEntity(specialistDTO);
//
//        // Handle profile image upload if necessary
//        if (profileImage != null && !profileImage.isEmpty()) {
//            // Assume uploadService exists to handle image uploads
//            Map<String, Object> uploadResult = uploadService.uploadImage(profileImage);
//            specialist.setAvatarUrl((String) uploadResult.get("url"));
//            specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
//        }
//
//        Specialist savedSpecialist = specialistRepository.save(specialist);
//        sendVerificationEmail(savedSpecialist);
//        return specialistMapper.toDTO(savedSpecialist);
//    }
//
//    private void sendVerificationEmail(Specialist specialist) throws MessagingException {
//        String token = UUID.randomUUID().toString();
//        // Save token in user
//        String to = specialist.getEmail();
//        String subject = "Email Verification";
//        String text = "Click the link to verify your email: http://yourdomain.com/verify?token=" + token;
//
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(text);
//
//        mailSender.send(message);
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
//    public SpecialistDTO updateSpecialist(Long id, String firstName, String lastName, String email, String password,
//                                          String mobileNumber, String speciality, String about,
//                                          Long pincode, String city, String state, String locality,
//                                          String country, MultipartFile profileImage) throws IOException {
//        Specialist specialist = specialistRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
//
//        // Update entity fields only if provided
//        if (firstName != null) specialist.setFirstName(firstName);
//        if (lastName != null) specialist.setLastName(lastName);
//        if (email != null) specialist.setEmail(email);
//        if (password != null) specialist.setPassword(passwordEncoder.encode(password));
//        if (mobileNumber != null) specialist.setMobileNumber(mobileNumber);
//        if (speciality != null) specialist.setSpeciality(speciality);
//        if (about != null) specialist.setAbout(about);
//
//        // Update address fields
//        if (specialist.getAddress() == null) {
//            specialist.setAddress(new Address());
//        }
//        if (pincode != null) specialist.getAddress().setPincode(pincode);
//        if (city != null) specialist.getAddress().setCity(city);
//        if (state != null) specialist.getAddress().setState(state);
//        if (locality != null) specialist.getAddress().setLocality(locality);
//        if (country != null) specialist.getAddress().setCountry(country);
//
//        // Handle profile image upload
//        if (profileImage != null && !profileImage.isEmpty()) {
//            Map<String, Object> uploadResult;
//            if (specialist.getAvatarPublicId() != null && !specialist.getAvatarPublicId().isEmpty()) {
//                uploadResult = uploadService.updateImage(specialist.getAvatarPublicId(), profileImage);
//            } else {
//                uploadResult = uploadService.uploadImage(profileImage);
//            }
//            specialist.setAvatarUrl((String) uploadResult.get("url"));
//            specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
//        }
//
//        Specialist updatedSpecialist = specialistRepository.save(specialist);
//        return specialistMapper.toDTO(updatedSpecialist);
//    }
//
//    @Transactional
//    public void deleteSpecialist(Long id) {
//        Specialist specialist = specialistRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
//        specialistRepository.deleteById(id);
//    }
//}

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

@Service
public class SpecialistService {

    private static final Logger log = LoggerFactory.getLogger(SpecialistService.class);


    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final SpecialistMapper specialistMapper;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    private final AddressRepository addressRepository;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, UserRepository userRepository, SpecialistMapper specialistMapper, PasswordEncoder passwordEncoder, UploadService uploadService, AddressRepository addressRepository) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.specialistMapper = specialistMapper;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public SpecialistDTO createSpecialist(SpecialistCreateRequestDTO specialistCreateRequestDTO, MultipartFile profileImage) {
        Optional<User> existingUser = userRepository.findByEmail(specialistCreateRequestDTO.getEmail());
        if (existingUser.isPresent() && existingUser.get().isVerified()) {
            throw new UserAlreadyExistsException("User with this email already exists and is verified.");
        } else if (existingUser.isPresent() && !existingUser.get().isVerified()) {
            throw new UserAlreadyExistsException("User with this email already exists but not verified.");
        }

        Specialist specialist = specialistMapper.toEntity(specialistCreateRequestDTO); // Use the correct method
        specialist.setPassword(passwordEncoder.encode(specialistCreateRequestDTO.getPassword()));

        Address address = specialist.getAddress();
        if (address != null) {
            address = addressRepository.save(address); // Save Address FIRST
            specialist.setAddress(address); // Associate saved Address
        }


        try {
            handleImageUpload(specialist, profileImage);
        } catch (IOException | ImageUploadException e) {
            log.error("Error handling image upload", e);
            throw new ImageUploadException("Error handling profile image", e);
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
        try {
            List<Specialist> specialists = specialistRepository.findAll();
            return specialists.stream()
                    .map(specialistMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Error retrieving specialists", e);
        }
    }

    public SpecialistDTO getSpecialistById(Long id) {
        try {
            Specialist specialist = specialistRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
            return specialistMapper.toDTO(specialist);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error retrieving specialist", e);
        }
    }

    @Transactional
    public SpecialistDTO updateSpecialist(Long id, SpecialistUpdateRequestDTO specialistUpdateRequestDTO, MultipartFile profileImage) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));

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
        if (specialistUpdateRequestDTO.getAvatarUrl() != null) {
            specialist.setAvatarUrl(specialistUpdateRequestDTO.getAvatarUrl());
        }
        if (specialistUpdateRequestDTO.getAvatarPublicId() != null) {
            specialist.setAvatarPublicId(specialistUpdateRequestDTO.getAvatarPublicId());
        }


        // Update Specialist fields
        if (specialistUpdateRequestDTO.getAbout() != null) {
            specialist.setAbout(specialistUpdateRequestDTO.getAbout());
        }
        if (specialistUpdateRequestDTO.getSpeciality() != null) {
            specialist.setSpeciality(specialistUpdateRequestDTO.getSpeciality());
        }


        // Update Address (if present in DTO)
        if (specialistUpdateRequestDTO.getPincode() != null ||
                specialistUpdateRequestDTO.getCity() != null ||
                specialistUpdateRequestDTO.getState() != null ||
                specialistUpdateRequestDTO.getLocality() != null ||
                specialistUpdateRequestDTO.getCountry() != null) {

            if (specialist.getAddress() == null) {
                specialist.setAddress(new Address()); // Create if it doesn't exist
            }
            Address address = specialist.getAddress();

            if (specialistUpdateRequestDTO.getPincode() != null) {
                address.setPincode(specialistUpdateRequestDTO.getPincode());
            }
            if (specialistUpdateRequestDTO.getCity() != null) {
                address.setCity(specialistUpdateRequestDTO.getCity());
            }
            if (specialistUpdateRequestDTO.getState() != null) {
                address.setState(specialistUpdateRequestDTO.getState());
            }
            if (specialistUpdateRequestDTO.getLocality() != null) {
                address.setLocality(specialistUpdateRequestDTO.getLocality());
            }
            if (specialistUpdateRequestDTO.getCountry() != null) {
                address.setCountry(specialistUpdateRequestDTO.getCountry());
            }
        }



        if (specialistUpdateRequestDTO.getPassword() != null) {
            specialist.setPassword(passwordEncoder.encode(specialistUpdateRequestDTO.getPassword()));
        }

        try {
            handleImageUpload(specialist, profileImage);
        } catch (IOException | ImageUploadException e) {
            log.error("Error handling image upload", e);
            throw new ImageUploadException("Error handling profile image", e);
        }


        try {
            Specialist updatedSpecialist = specialistRepository.save(specialist);
            return specialistMapper.toDTO(updatedSpecialist);
        } catch (Exception e) {
            log.error("Error updating specialist", e);
            throw e;
        }
    }

    @Transactional
    public void deleteSpecialist(Long id) {
        try {
            Specialist specialist = specialistRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));
            specialistRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error deleting specialist", e);
        }
    }
}
