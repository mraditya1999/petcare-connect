//package com.petconnect.backend.controllers;
//
//import com.petconnect.backend.dto.ApiResponse;
//import com.petconnect.backend.dto.SpecialistDTO;
//import com.petconnect.backend.dto.SpecialistCreateRequestDTO;
//import com.petconnect.backend.dto.SpecialistResponseDTO;
//import com.petconnect.backend.dto.SpecialistUpdateRequestDTO;
//import com.petconnect.backend.dto.AddressDTO;
//import com.petconnect.backend.mappers.SpecialistMapper;
//import com.petconnect.backend.services.SpecialistService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@RestController
//@RequestMapping("/specialists")
//public class SpecialistController {
//
//    private static final Logger log = LoggerFactory.getLogger(SpecialistController.class);
//
//    private final SpecialistService specialistService;
//    private final SpecialistMapper specialistMapper;
//
//    @Autowired
//    public SpecialistController(SpecialistService specialistService, SpecialistMapper specialistMapper) {
//        this.specialistService = specialistService;
//        this.specialistMapper = specialistMapper;
//    }
//
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<SpecialistDTO> createSpecialist(
//            @RequestParam("firstName") String firstName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("email") String email,
//            @RequestParam("password") String password,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("speciality") String speciality,
//            @RequestParam("about") String about,
//            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
//            @RequestParam("pincode") Long pincode,
//            @RequestParam("city") String city,
//            @RequestParam("state") String state,
//            @RequestParam("locality") String locality,
//            @RequestParam("country") String country) throws IOException {
//
//        SpecialistCreateRequestDTO requestDTO = new SpecialistCreateRequestDTO();
//        requestDTO.setFirstName(firstName);
//        requestDTO.setLastName(lastName);
//        requestDTO.setEmail(email);
//        requestDTO.setPassword(password);
//        requestDTO.setMobileNumber(mobileNumber);
//        requestDTO.setSpeciality(speciality);
//        requestDTO.setAbout(about);
//
//        AddressDTO addressDTO = new AddressDTO();
//        addressDTO.setPincode(pincode);
//        addressDTO.setCity(city);
//        addressDTO.setState(state);
//        addressDTO.setLocality(locality);
//        addressDTO.setCountry(country);
//        requestDTO.setAddressDTO(addressDTO);
//
//        SpecialistDTO createdSpecialist = specialistService.createSpecialist(requestDTO, profileImage);
//        return ResponseEntity.ok(createdSpecialist);
//    }
//
//    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> updateCurrentSpecialist(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestParam(value = "firstName", required = false) String firstName,
//            @RequestParam(value = "lastName", required = false) String lastName,
//            @RequestParam(value = "email", required = false) String email,
//            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
//            @RequestParam(value = "speciality", required = false) String speciality,
//            @RequestParam(value = "about", required = false) String about,
//            @RequestParam(value = "pincode", required = false) Long pincode,
//            @RequestParam(value = "city", required = false) String city,
//            @RequestParam(value = "state", required = false) String state,
//            @RequestParam(value = "locality", required = false) String locality,
//            @RequestParam(value = "country", required = false) String country,
//            @RequestParam(value = "password", required = false) String password,
//            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
//
//        SpecialistUpdateRequestDTO specialistUpdateRequestDTO = new SpecialistUpdateRequestDTO();
//        specialistUpdateRequestDTO.setFirstName(firstName);
//        specialistUpdateRequestDTO.setLastName(lastName);
//        specialistUpdateRequestDTO.setEmail(email);
//        specialistUpdateRequestDTO.setMobileNumber(mobileNumber);
//        specialistUpdateRequestDTO.setSpeciality(speciality);
//        specialistUpdateRequestDTO.setAbout(about);
//        specialistUpdateRequestDTO.setPassword(password);
//
//        AddressDTO addressDTO = new AddressDTO();
//        addressDTO.setPincode(pincode);
//        addressDTO.setCity(city);
//        addressDTO.setState(state);
//        addressDTO.setLocality(locality);
//        addressDTO.setCountry(country);
//        specialistUpdateRequestDTO.setAddressDTO(addressDTO);
//
//        SpecialistDTO updatedSpecialist = specialistService.updateSpecialist(specialistUpdateRequestDTO, profileImage, userDetails);
//        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(updatedSpecialist);
//        return ResponseEntity.ok(new ApiResponse<>("Specialist updated successfully", specialistResponseDTO));
//    }
//
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<List<SpecialistResponseDTO>>> getAllSpecialists() {
//        List<SpecialistDTO> specialists = specialistService.getAllSpecialists();
//        List<SpecialistResponseDTO> specialistResponseDTOS = specialists.stream()
//                .map(specialistDTO -> specialistMapper.toSpecialistResponseDTO(specialistDTO)) // Use a lambda function
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(new ApiResponse<>("Specialists retrieved successfully", specialistResponseDTOS));
//    }
//
//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> getSpecialistById(@PathVariable Long id) {
//        SpecialistDTO specialist = specialistService.getSpecialistById(id);
//        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(specialist);
//        return ResponseEntity.ok(new ApiResponse<>("Specialist retrieved successfully", specialistResponseDTO));
//    }
//
//
//    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<Void>> deleteCurrentSpecialist(@AuthenticationPrincipal UserDetails userDetails) {
//        specialistService.deleteCurrentSpecialist(userDetails);
//        return ResponseEntity.noContent().build();
//    }
//}

//##################################################################################################################
//package com.petconnect.backend.controllers;
//
//import com.petconnect.backend.dto.*;
//import com.petconnect.backend.exceptions.ResourceNotFoundException;
//import com.petconnect.backend.mappers.SpecialistMapper;
//import com.petconnect.backend.services.SpecialistService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/specialists")
//public class SpecialistController {
//
//    private static final Logger log = LoggerFactory.getLogger(SpecialistController.class);
//
//    private final SpecialistService specialistService;
//    private final SpecialistMapper specialistMapper;
//    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
//
//    @Autowired
//    public SpecialistController(SpecialistService specialistService, SpecialistMapper specialistMapper) {
//        this.specialistService = specialistService;
//        this.specialistMapper = specialistMapper;
//    }
//
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse<SpecialistDTO>> createSpecialist(
//            @RequestParam("firstName") String firstName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("email") String email,
//            @RequestParam("password") String password,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("speciality") String speciality,
//            @RequestParam("about") String about,
//            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
//            @RequestParam("pincode") Long pincode,
//            @RequestParam("city") String city,
//            @RequestParam("state") String state,
//            @RequestParam("locality") String locality,
//            @RequestParam("country") String country) throws IOException {
//
//        SpecialistCreateRequestDTO requestDTO = new SpecialistCreateRequestDTO();
//        requestDTO.setFirstName(firstName);
//        requestDTO.setLastName(lastName);
//        requestDTO.setEmail(email);
//        requestDTO.setPassword(password);
//        requestDTO.setMobileNumber(mobileNumber);
//        requestDTO.setSpeciality(speciality);
//        requestDTO.setAbout(about);
//
//        AddressDTO addressDTO = new AddressDTO();
//        addressDTO.setPincode(pincode);
//        addressDTO.setCity(city);
//        addressDTO.setState(state);
//        addressDTO.setLocality(locality);
//        addressDTO.setCountry(country);
//        requestDTO.setAddressDTO(addressDTO);
//
//        SpecialistDTO createdSpecialistDTO = specialistService.createSpecialist(requestDTO, profileImage);
//        return ResponseEntity.ok(new ApiResponse<>("Specialist created successfully. Verification email sent.", createdSpecialistDTO));
//    }
//
//    @PostMapping("/verify-email")
//    public ResponseEntity<ApiResponse<String>> verifySpecialist(@Valid @RequestBody Map<String, String> request) {
//        String token = request.get("verificationToken");
//        boolean isVerified = specialistService.verifySpecialist(token);
//
//        if (isVerified) {
//            logger.info("Specialist verified successfully with token: {}", token);
//            ApiResponse<String> apiResponse = new ApiResponse<>("Specialist verified and registered successfully.");
//            return ResponseEntity.ok(apiResponse);
//        } else {
//            logger.warn("Invalid or expired verification token: {}", token);
//            throw new ResourceNotFoundException("Invalid or expired verification token.");
//        }
//    }
//
//    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> updateCurrentSpecialist(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestParam(value = "firstName", required = false) String firstName,
//            @RequestParam(value = "lastName", required = false) String lastName,
//            @RequestParam(value = "email", required = false) String email,
//            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
//            @RequestParam(value = "speciality", required = false) String speciality,
//            @RequestParam(value = "about", required = false) String about,
//            @RequestParam(value = "pincode", required = false) Long pincode,
//            @RequestParam(value = "city", required = false) String city,
//            @RequestParam(value = "state", required = false) String state,
//            @RequestParam(value = "locality", required = false) String locality,
//            @RequestParam(value = "country", required = false) String country,
//            @RequestParam(value = "password", required = false) String password,
//            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
//
//        SpecialistUpdateRequestDTO specialistUpdateRequestDTO = new SpecialistUpdateRequestDTO();
//        specialistUpdateRequestDTO.setFirstName(firstName);
//        specialistUpdateRequestDTO.setLastName(lastName);
//        specialistUpdateRequestDTO.setEmail(email);
//        specialistUpdateRequestDTO.setMobileNumber(mobileNumber);
//        specialistUpdateRequestDTO.setSpeciality(speciality);
//        specialistUpdateRequestDTO.setAbout(about);
//        specialistUpdateRequestDTO.setPassword(password);
//
//        AddressDTO addressDTO = new AddressDTO();
//        addressDTO.setPincode(pincode);
//        addressDTO.setCity(city);
//        addressDTO.setState(state);
//        addressDTO.setLocality(locality);
//        addressDTO.setCountry(country);
//        specialistUpdateRequestDTO.setAddressDTO(addressDTO);
//
//        SpecialistDTO updatedSpecialist = specialistService.updateSpecialist(specialistUpdateRequestDTO, profileImage, userDetails);
//        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(updatedSpecialist);
//        return ResponseEntity.ok(new ApiResponse<>("Specialist updated successfully", specialistResponseDTO));
//    }
//
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<List<SpecialistResponseDTO>>> getAllSpecialists() {
//        List<SpecialistDTO> specialists = specialistService.getAllSpecialists();
//        List<SpecialistResponseDTO> specialistResponseDTOS = specialists.stream()
//                .map(specialistDTO -> specialistMapper.toSpecialistResponseDTO(specialistDTO))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(new ApiResponse<>("Specialists retrieved successfully", specialistResponseDTOS));
//    }
//
//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> getSpecialistById(@PathVariable Long id) {
//        SpecialistDTO specialist = specialistService.getSpecialistById(id);
//        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(specialist);
//        return ResponseEntity.ok(new ApiResponse<>("Specialist retrieved successfully", specialistResponseDTO));
//    }
//
//    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<Void>> deleteCurrentSpecialist(@AuthenticationPrincipal UserDetails userDetails) {
//        specialistService.deleteCurrentSpecialist(userDetails);
//        return ResponseEntity.noContent().build();
//    }
//}


package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.services.SpecialistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private static final Logger log = LoggerFactory.getLogger(SpecialistController.class);

    private final SpecialistService specialistService;
    private final SpecialistMapper specialistMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public SpecialistController(SpecialistService specialistService, SpecialistMapper specialistMapper) {
        this.specialistService = specialistService;
        this.specialistMapper = specialistMapper;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> createSpecialist(
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

        SpecialistDTO createdSpecialist = specialistService.createSpecialist(requestDTO, profileImage);

        // Check if profileImage was uploaded and set avatar URL and public ID
        if (profileImage != null && !profileImage.isEmpty()) {
            // Assuming you have a method to handle image upload and get URL & public ID
            Map<String, String> imageInfo = specialistService.handleProfileImageUpload(profileImage);
            createdSpecialist.setAvatarUrl(imageInfo.get("avatarUrl"));
            createdSpecialist.setAvatarPublicId(imageInfo.get("avatarPublicId"));
        }

        return ResponseEntity.ok(new ApiResponse<>("Specialist created successfully. Verification email sent."));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifySpecialist(@Valid @RequestBody Map<String, String> request) {
        String token = request.get("verificationToken");
        boolean isVerified = specialistService.verifySpecialist(token);

        if (isVerified) {
            logger.info("Specialist verified successfully with token: {}", token);
            ApiResponse<String> apiResponse = new ApiResponse<>("Specialist verified and registered successfully.");
            return ResponseEntity.ok(apiResponse);
        } else {
            logger.warn("Invalid or expired verification token: {}", token);
            throw new ResourceNotFoundException("Invalid or expired verification token.");
        }
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> updateCurrentSpecialist(
            @AuthenticationPrincipal UserDetails userDetails,
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

        SpecialistDTO updatedSpecialist = specialistService.updateSpecialist(specialistUpdateRequestDTO, profileImage, userDetails);
        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(updatedSpecialist);
        return ResponseEntity.ok(new ApiResponse<>("Specialist updated successfully", specialistResponseDTO));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<SpecialistResponseDTO>>> getAllSpecialists() {
        List<SpecialistDTO> specialists = specialistService.getAllSpecialists();
        List<SpecialistResponseDTO> specialistResponseDTOS = specialists.stream()
                .map(specialistDTO -> specialistMapper.toSpecialistResponseDTO(specialistDTO))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Specialists retrieved successfully", specialistResponseDTOS));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> getSpecialistById(@PathVariable Long id) {
        SpecialistDTO specialist = specialistService.getSpecialistById(id);
        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(specialist);
        return ResponseEntity.ok(new ApiResponse<>("Specialist retrieved successfully", specialistResponseDTO));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteCurrentSpecialist(@AuthenticationPrincipal UserDetails userDetails) {
        specialistService.deleteCurrentSpecialist(userDetails);
        return ResponseEntity.noContent().build();
    }
}
