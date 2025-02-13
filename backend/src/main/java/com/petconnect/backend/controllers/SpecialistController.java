package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.mappers.SpecialistMapper;
import com.petconnect.backend.services.SpecialistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import com.petconnect.backend.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private static final Logger log = LoggerFactory.getLogger(SpecialistController.class);

    private final SpecialistService specialistService;
    private final SpecialistMapper specialistMapper;

    @Autowired
    public SpecialistController(SpecialistService specialistService, SpecialistMapper specialistMapper) {
        this.specialistService = specialistService;
        this.specialistMapper = specialistMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SpecialistDTO> createSpecialist(
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
            @RequestParam("country") String country
    ) throws IOException {

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
        return ResponseEntity.ok(createdSpecialist);
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
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

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
                .map(specialistMapper::toSpecialistResponseDTO)
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