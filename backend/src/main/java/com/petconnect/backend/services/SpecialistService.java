package com.petconnect.backend.services;//package com.petconnect.backend.services;

import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.repositories.SpecialistRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    private final SpecialistRepository specialistRepository;
    private final SpecialistMapper specialistMapper;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UploadService uploadService;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, SpecialistMapper specialistMapper, JavaMailSender mailSender, PasswordEncoder passwordEncoder, UploadService uploadService) {
        this.specialistRepository = specialistRepository;
        this.specialistMapper = specialistMapper;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.uploadService = uploadService;
    }

    @Transactional
    public SpecialistDTO createSpecialist(SpecialistDTO specialistDTO, MultipartFile profileImage) {
        try {
            specialistDTO.setPassword(passwordEncoder.encode(specialistDTO.getPassword()));
            Specialist specialist = specialistMapper.toEntity(specialistDTO);

            // Handle profile image upload if necessary
            if (profileImage != null && !profileImage.isEmpty()) {
                // Assume uploadService exists to handle image uploads
                Map<String, Object> uploadResult = uploadService.uploadImage(profileImage);
                specialist.setAvatarUrl((String) uploadResult.get("url"));
                specialist.setAvatarPublicId((String) uploadResult.get("public_id"));
            }

            Specialist savedSpecialist = specialistRepository.save(specialist);
            sendVerificationEmail(savedSpecialist);
            return specialistMapper.toDTO(savedSpecialist);
        } catch (MessagingException | IOException e) {
            throw new ServiceException("Error creating specialist", e);
        }
    }

    private void sendVerificationEmail(Specialist specialist) throws MessagingException {
        String token = UUID.randomUUID().toString();
        // Save token in user
        String to = specialist.getEmail();
        String subject = "Email Verification";
        String text = "Click the link to verify your email: http://yourdomain.com/verify?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        mailSender.send(message);
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
    public SpecialistDTO updateSpecialist(Long id, String firstName, String lastName, String email, String password,
                                          String mobileNumber, String speciality, String about,
                                          Long pincode, String city, String state, String locality,
                                          String country, MultipartFile profileImage) {
        try {
            Specialist specialist = specialistRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with id " + id));

            // Update entity fields only if provided
            if (firstName != null) specialist.setFirstName(firstName);
            if (lastName != null) specialist.setLastName(lastName);
            if (email != null) specialist.setEmail(email);
            if (password != null) specialist.setPassword(passwordEncoder.encode(password));
            if (mobileNumber != null) specialist.setMobileNumber(mobileNumber);
            if (speciality != null) specialist.setSpeciality(speciality);
            if (about != null) specialist.setAbout(about);

            // Update address fields
            if (specialist.getAddress() == null) {
                specialist.setAddress(new Address());
            }
            if (pincode != null) specialist.getAddress().setPincode(pincode);
            if (city != null) specialist.getAddress().setCity(city);
            if (state != null) specialist.getAddress().setState(state);
            if (locality != null) specialist.getAddress().setLocality(locality);
            if (country != null) specialist.getAddress().setCountry(country);

            // Handle profile image upload
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

            Specialist updatedSpecialist = specialistRepository.save(specialist);
            return specialistMapper.toDTO(updatedSpecialist);
        } catch (IOException e) {
            throw new ServiceException("Error updating specialist", e);
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
