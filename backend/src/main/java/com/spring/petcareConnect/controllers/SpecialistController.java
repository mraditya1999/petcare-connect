package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.specialist.request.AdminSpecialistUpdateRequest;
import com.spring.petcareConnect.dtos.specialist.request.SpecialistProfileUpdateRequestDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistDashboardDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistListResponseDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistResponseDto;
import com.spring.petcareConnect.services.SpecialistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<SpecialistListResponseDto>> getSpecialists(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                                       @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                                       @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_SPECIALISTS_BY, required = false) String sortBy,
                                                                                       @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        SpecialistListResponseDto specialistListResponseDto = specialistService.getSpecialists(pageNumber, pageSize, sortBy, sortOrder);
        CustomApiResponse<SpecialistListResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.ALL_SPECIALISTS_FETCHED, specialistListResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{specialistId}")
    public ResponseEntity<CustomApiResponse<SpecialistResponseDto>> getSpecialistById(@PathVariable Long specialistId) {
        SpecialistResponseDto specialistResponseDto = specialistService.getSpecialistById(specialistId);
        CustomApiResponse<SpecialistResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.SPECIALIST_FETCHED, specialistResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<CustomApiResponse<SpecialistDashboardDto>> getSpecialistDashboard() {
        SpecialistDashboardDto specialistDashboardDto = specialistService.getSpecialistDashboard();
        CustomApiResponse<SpecialistDashboardDto> response = new CustomApiResponse<>(true, ResponseMessages.SPECIALIST_DASHBOARD_FETCHED, specialistDashboardDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/me")
    public ResponseEntity<CustomApiResponse<SpecialistResponseDto>> updateCurrentSpecialist(@Valid @RequestBody SpecialistProfileUpdateRequestDto request) {
        SpecialistResponseDto specialistResponseDto = specialistService.updateCurrentSpecialist(request);
        CustomApiResponse<SpecialistResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.SPECIALIST_UPDATED, specialistResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/admin/{specialistId}")
    public ResponseEntity<CustomApiResponse<SpecialistResponseDto>> updateSpecialistByAdmin(@PathVariable Long specialistId,
                                                                                         @Valid @RequestBody AdminSpecialistUpdateRequest request) {
        SpecialistResponseDto specialistResponseDto = specialistService.updateSpecialistByAdmin(specialistId, request);
        CustomApiResponse<SpecialistResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.SPECIALIST_UPDATED, specialistResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
