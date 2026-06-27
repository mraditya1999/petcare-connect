package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.pet.request.PetRequestDto;
import com.spring.petcareConnect.dtos.pet.response.PetListResponseDto;
import com.spring.petcareConnect.dtos.pet.response.PetResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PetService {
    PetResponseDto createPetForUser(PetRequestDto petRequestDTO, MultipartFile profileImage);

    PetResponseDto updatePetForUser(Long petId, PetRequestDto petRequestDTO, MultipartFile profileImage);

    PetListResponseDto getAllPetsForUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    PetResponseDto getPetOfUserById(Long petId);

    void deletePetForUser(Long petId);
}
