package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.*;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.services.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    @GetMapping("/specialists")
    public ResponseEntity<ApiResponse<Page<SpecialistDTO>>> getAllSpecialists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "specialistId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            Page<SpecialistDTO> specialists = specialistService.getAllSpecialists(pageable);
            logger.info("Fetched all specialists with pagination and sorting");
            return ResponseEntity.ok(new ApiResponse<>("Fetched all specialists", specialists));
        } catch (Exception e) {
            logger.error("Error fetching specialists: {}", e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error fetching specialists"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/specialists/{id}")
    public ResponseEntity<ApiResponse<SpecialistDTO>> getSpecialistById(@PathVariable Long id) {
        try {
            SpecialistDTO specialist = specialistService.getSpecialistById(id);
            logger.info("Fetched specialist with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("Fetched specialist", specialist));
        } catch (Exception e) {
            logger.error("Error fetching specialist with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Specialist not found"), HttpStatus.NOT_FOUND);
        }
    }
}
