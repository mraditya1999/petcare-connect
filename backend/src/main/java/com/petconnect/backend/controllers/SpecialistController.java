package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.AddressDTO;
import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.dto.UserRegistrationRequest;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.services.AuthService;
import com.petconnect.backend.services.SpecialistService;
import com.petconnect.backend.services.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//@RestController
//@RequestMapping("/specialists")
//public class SpecialistController {
//
//    private final SpecialistService specialistService;
//
//    @Autowired
//    public SpecialistController(SpecialistService specialistService) {
//        this.specialistService = specialistService;
//    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SpecialistDTO> createSpecialist(
//            @RequestParam("firstName") String firstName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("email") String email,
//            @RequestParam("password") String password,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("speciality") String speciality,
//            @RequestParam("about") String about,
//            @RequestParam("pincode") Long pincode,
//            @RequestParam("city") String city,
//            @RequestParam("state") String state,
//            @RequestParam("locality") String locality,
//            @RequestParam("country") String country,
//            @RequestParam("profileImage") MultipartFile profileImage) throws IOException {
//
//        AddressDTO addressDTO = new AddressDTO();
//        addressDTO.setPincode(pincode);
//        addressDTO.setCity(city);
//        addressDTO.setState(state);
//        addressDTO.setLocality(locality);
//        addressDTO.setCountry(country);
//
//        SpecialistDTO specialistDTO = new SpecialistDTO();
//        specialistDTO.setFirstName(firstName);
//        specialistDTO.setLastName(lastName);
//        specialistDTO.setEmail(email);
//        specialistDTO.setPassword(password);
//        specialistDTO.setMobileNumber(mobileNumber);
//        specialistDTO.setSpeciality(speciality);
//        specialistDTO.setAbout(about);
//        specialistDTO.setAddress(addressDTO);
//
//        SpecialistDTO createdSpecialist = specialistService.createSpecialist(specialistDTO, profileImage);
//        return ResponseEntity.status(201).body(createdSpecialist);
//    }
//
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<SpecialistDTO>> getAllSpecialists() {
//        List<SpecialistDTO> specialists = specialistService.getAllSpecialists();
//        return ResponseEntity.ok(specialists);
//    }
//
//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SpecialistDTO> getSpecialistById(@PathVariable Long id) {
//        SpecialistDTO specialist = specialistService.getSpecialistById(id);
//        return ResponseEntity.ok(specialist);
//    }
//
//    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SpecialistDTO> updateSpecialist(
//            @PathVariable Long id,
//            @RequestParam(value = "firstName", required = false) String firstName,
//            @RequestParam(value = "lastName", required = false) String lastName,
//            @RequestParam(value = "email", required = false) String email,
//            @RequestParam(value = "password", required = false) String password,
//            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
//            @RequestParam(value = "speciality", required = false) String speciality,
//            @RequestParam(value = "about", required = false) String about,
//            @RequestParam(value = "pincode", required = false) Long pincode,
//            @RequestParam(value = "city", required = false) String city,
//            @RequestParam(value = "state", required = false) String state,
//            @RequestParam(value = "locality", required = false) String locality,
//            @RequestParam(value = "country", required = false) String country,
//            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
//
//        SpecialistDTO updatedSpecialist = specialistService.updateSpecialist(id, firstName, lastName, email, password, mobileNumber, speciality, about, pincode, city, state, locality, country, profileImage);
//        return ResponseEntity.ok(updatedSpecialist);
//    }
//
//        @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> deleteSpecialist(@PathVariable Long id) {
//        specialistService.deleteSpecialist(id);
//        return ResponseEntity.noContent().build();
//    }
//}
//#######################################################################################
//@RestController
//@RequestMapping("/specialists")
//public class SpecialistController {
//
//    private final AuthService authService;
//    private final UserService userService;
//    private final SpecialistService specialistService;
//    private final RoleRepository roleRepository;
//
//    @Autowired
//    public SpecialistController(AuthService authService, UserService userService, SpecialistService specialistService, RoleRepository roleRepository) {
//        this.authService = authService;
//        this.userService = userService;
//        this.specialistService = specialistService;
//        this.roleRepository = roleRepository;
//    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<SpecialistDTO> createSpecialist(
//            @RequestParam("firstName") String firstName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("email") String email,
//            @RequestParam("password") String password,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("speciality") String speciality,
//            @RequestParam("about") String about,
//            @RequestParam("pincode") Long pincode,
//            @RequestParam("city") String city,
//            @RequestParam("state") String state,
//            @RequestParam("locality") String locality,
//            @RequestParam("country") String country,
//            @RequestParam("profileImage") MultipartFile profileImage) throws IOException, MessagingException {
//
//        Optional<User> existingUser = userService.findByEmail(email);
//
//        if (existingUser.isPresent() && existingUser.get().isVerified()) {
//            SpecialistDTO specialistDTO = new SpecialistDTO();
//            specialistDTO.setFirstName(firstName);
//            specialistDTO.setLastName(lastName);
//            specialistDTO.setEmail(email);
//            specialistDTO.setPassword(password);
//            specialistDTO.setMobileNumber(mobileNumber);
//            specialistDTO.setSpeciality(speciality);
//            specialistDTO.setAbout(about);
//
//            AddressDTO addressDTO = new AddressDTO();
//            addressDTO.setPincode(pincode);
//            addressDTO.setCity(city);
//            addressDTO.setState(state);
//            addressDTO.setLocality(locality);
//            addressDTO.setCountry(country);
//            specialistDTO.setAddress(addressDTO);
//
//            specialistDTO = specialistService.createSpecialist(specialistDTO, profileImage);
//
//            return ResponseEntity.status(201).body(specialistDTO);
//        } else if (existingUser.isPresent() && !existingUser.get().isVerified()) {
//            throw new MessagingException("User already exists but email is not verified.");
//        } else {
//            User user = new User();
//            user.setEmail(email);
//            user.setPassword(password);
//            user.setFirstName(firstName);
//            user.setLastName(lastName);
//
//            // Set roles
//            Optional<Role> userRoleOptional = roleRepository.findByRoleName(Role.RoleName.USER);
//            Optional<Role> specialistRoleOptional = roleRepository.findByRoleName(Role.RoleName.SPECIALIST);
//
//            if (userRoleOptional.isPresent() && specialistRoleOptional.isPresent()) {
//                Role userRole = userRoleOptional.get();
//                Role specialistRole = specialistRoleOptional.get();
//                user.setRoles(Set.of(userRole, specialistRole));
//            } else {
//                throw new RuntimeException("Roles not found");
//            }
//
//            authService.registerUser(user);
//            throw new MessagingException("User created but email verification is required.");
//        }
//    }
//
//
//    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SpecialistDTO> updateSpecialist(
//            @PathVariable Long id,
//            @RequestParam(value = "firstName", required = false) String firstName,
//            @RequestParam(value = "lastName", required = false) String lastName,
//            @RequestParam(value = "email", required = false) String email,
//            @RequestParam(value = "password", required = false) String password,
//            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
//            @RequestParam(value = "speciality", required = false) String speciality,
//            @RequestParam(value = "about", required = false) String about,
//            @RequestParam(value = "pincode", required = false) Long pincode,
//            @RequestParam(value = "city", required = false) String city,
//            @RequestParam(value = "state", required = false) String state,
//            @RequestParam(value = "locality", required = false) String locality,
//            @RequestParam(value = "country", required = false) String country,
//            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
//
//        SpecialistDTO updatedSpecialist = specialistService.updateSpecialist(
//                id, firstName, lastName, email, password, mobileNumber,
//                speciality, about, pincode, city, state, locality, country, profileImage);
//
//        return ResponseEntity.ok(updatedSpecialist);
//    }
//
//    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> deleteSpecialist(@PathVariable Long id) {
//        specialistService.deleteSpecialist(id);
//        return ResponseEntity.noContent().build();
//    }
//}

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private final AuthService authService;
    private final UserService userService;
    private final SpecialistService specialistService;
    private final RoleRepository roleRepository;

    @Autowired
    public SpecialistController(AuthService authService, UserService userService, SpecialistService specialistService, RoleRepository roleRepository) {
        this.authService = authService;
        this.userService = userService;
        this.specialistService = specialistService;
        this.roleRepository = roleRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpecialistDTO>> createSpecialist(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam("speciality") String speciality,
            @RequestParam("about") String about,
            @RequestParam("pincode") Long pincode,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("locality") String locality,
            @RequestParam("country") String country,
            @RequestParam("profileImage") MultipartFile profileImage) {

        try {
            Optional<User> existingUser = userService.findByEmail(email);

            if (existingUser.isPresent() && existingUser.get().isVerified()) {
                SpecialistDTO specialistDTO = new SpecialistDTO();
                specialistDTO.setFirstName(firstName);
                specialistDTO.setLastName(lastName);
                specialistDTO.setEmail(email);
                specialistDTO.setPassword(password);
                specialistDTO.setMobileNumber(mobileNumber);
                specialistDTO.setSpeciality(speciality);
                specialistDTO.setAbout(about);

                AddressDTO addressDTO = new AddressDTO();
                addressDTO.setPincode(pincode);
                addressDTO.setCity(city);
                addressDTO.setState(state);
                addressDTO.setLocality(locality);
                addressDTO.setCountry(country);
                specialistDTO.setAddress(addressDTO);

                specialistDTO = specialistService.createSpecialist(specialistDTO, profileImage);

                ApiResponse<SpecialistDTO> response = new ApiResponse<>("Specialist created successfully", specialistDTO);
                return ResponseEntity.status(201).body(response);
            } else if (existingUser.isPresent() && !existingUser.get().isVerified()) {
                throw new MessagingException("User already exists but email is not verified.");
            } else {
                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);

                // Set roles
                Optional<Role> userRoleOptional = roleRepository.findByRoleName(Role.RoleName.USER);
                Optional<Role> specialistRoleOptional = roleRepository.findByRoleName(Role.RoleName.SPECIALIST);

                if (userRoleOptional.isPresent() && specialistRoleOptional.isPresent()) {
                    Role userRole = userRoleOptional.get();
                    Role specialistRole = specialistRoleOptional.get();
                    user.setRoles(Set.of(userRole, specialistRole));
                } else {
                    throw new RuntimeException("Roles not found");
                }

                authService.registerUser(user);
                throw new MessagingException("User created but email verification is required.");
            }
        } catch (MessagingException | RuntimeException e) {
            ApiResponse<SpecialistDTO> response = new ApiResponse<>(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SpecialistDTO>> updateSpecialist(
            @PathVariable Long id,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "speciality", required = false) String speciality,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam(value = "pincode", required = false) Long pincode,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "locality", required = false) String locality,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        SpecialistDTO updatedSpecialist = specialistService.updateSpecialist(
                id, firstName, lastName, email, password, mobileNumber,
                speciality, about, pincode, city, state, locality, country, profileImage);

        ApiResponse<SpecialistDTO> response = new ApiResponse<>("Specialist updated successfully", updatedSpecialist);
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SpecialistDTO>>> getAllSpecialists() {
        List<SpecialistDTO> specialists = specialistService.getAllSpecialists();
        ApiResponse<List<SpecialistDTO>> response = new ApiResponse<>("Specialists retrieved successfully", specialists);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteSpecialist(@PathVariable Long id) {
        try {
            specialistService.deleteSpecialist(id);
            ApiResponse<Void> response = new ApiResponse<>("Specialist deleted successfully");
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            ApiResponse<Void> response = new ApiResponse<>(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
