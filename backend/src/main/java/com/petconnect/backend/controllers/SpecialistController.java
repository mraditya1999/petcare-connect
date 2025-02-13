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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> createSpecialist(
            @Valid @ModelAttribute SpecialistCreateRequestDTO specialistCreateRequestDTO,
            BindingResult bindingResult,
            @RequestParam("profileImage") MultipartFile profileImage) {

        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult, SpecialistResponseDTO.class); // Pass the type
        }

        SpecialistDTO specialistDTO = specialistService.createSpecialist(specialistCreateRequestDTO, profileImage);
        SpecialistResponseDTO specialistResponseDTO = specialistMapper.toSpecialistResponseDTO(specialistDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Specialist created successfully", specialistResponseDTO));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SpecialistResponseDTO>> updateSpecialist(
            @PathVariable Long id,
            @Valid @ModelAttribute SpecialistUpdateRequestDTO specialistUpdateRequestDTO,
            BindingResult bindingResult,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult, SpecialistResponseDTO.class);
        }

        SpecialistDTO updatedSpecialist = specialistService.updateSpecialist(id, specialistUpdateRequestDTO, profileImage);
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

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteSpecialist(@PathVariable Long id) {
        specialistService.deleteSpecialist(id);
        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<ApiResponse<T>> validationErrorResponse(BindingResult bindingResult, Class<T> type) {
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new ApiResponse<>("Validation errors"));
    }



}