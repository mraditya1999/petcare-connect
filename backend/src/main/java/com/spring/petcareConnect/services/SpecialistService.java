package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.specialist.request.AdminSpecialistUpdateRequest;
import com.spring.petcareConnect.dtos.specialist.request.SpecialistProfileUpdateRequestDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistDashboardDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistListResponseDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistResponseDto;

public interface SpecialistService {
    SpecialistListResponseDto getSpecialists(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    SpecialistResponseDto getSpecialistById(Long specialistId);

    SpecialistDashboardDto getSpecialistDashboard();

    SpecialistResponseDto updateSpecialistByAdmin(Long specialistId, AdminSpecialistUpdateRequest request);

    SpecialistResponseDto updateCurrentSpecialist(SpecialistProfileUpdateRequestDto request);
}
