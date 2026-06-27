package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.pet.request.PetRequestDto;
import com.spring.petcareConnect.dtos.pet.response.PetListResponseDto;
import com.spring.petcareConnect.dtos.pet.response.PetResponseDto;
import com.spring.petcareConnect.services.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<PetResponseDto>> createPetForUser(@Valid @ModelAttribute PetRequestDto petRequestDTO, @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        PetResponseDto petResponseDTO = petService.createPetForUser(petRequestDTO, profileImage);
        CustomApiResponse<PetResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.PET_CREATED, petResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{petId}")
    public ResponseEntity<CustomApiResponse<PetResponseDto>> updatePetForUser(@PathVariable Long petId, @Valid @ModelAttribute PetRequestDto petRequestDTO,
             @RequestParam(value = "profileImage", required = false) MultipartFile profileImage){
        PetResponseDto petResponseDTO = petService.updatePetForUser(petId, petRequestDTO, profileImage);
        CustomApiResponse<PetResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.PET_UPDATED, petResponseDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<PetListResponseDto>> getAllPetsForUser(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                                   @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                                   @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PETS_BY, required = false) String sortBy,
                                                                                   @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        PetListResponseDto petListResponseDto = petService.getAllPetsForUser(pageNumber, pageSize, sortBy, sortOrder);
        CustomApiResponse<PetListResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.ALL_PETS_FETCHED, petListResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<CustomApiResponse<PetResponseDto>> getPetOfUserById(@PathVariable Long petId) {
            PetResponseDto petResponseDto = petService.getPetOfUserById(petId);
        CustomApiResponse<PetResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.PET_FETCHED, petResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<CustomApiResponse<String>> deletePetForUser(@PathVariable Long petId) {
        petService.deletePetForUser(petId);
        CustomApiResponse<String> response =
                new CustomApiResponse<>(true, ResponseMessages.PET_DELETED, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}