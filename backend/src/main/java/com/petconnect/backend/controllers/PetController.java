//package com.petconnect.backend.controllers;
//
//import com.petconnect.backend.dto.PetDTO;
//import com.petconnect.backend.entity.Pet;
//import com.petconnect.backend.services.PetService;
//import com.petconnect.backend.services.UploadService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/pets")
//public class PetController {
//
//    @Autowired
//    private PetService petService;
//
//    @Autowired
//    private UploadService uploadService;
//
//    @PostMapping
//    public ResponseEntity<PetDTO> createPet(@Valid @RequestBody PetDTO petRequest) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Pet createdPet = petService.createPetForUser(petRequest, username);
//        PetDTO petDTO = petService.convertToDto(createdPet);
//        return new ResponseEntity<>(petDTO, HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<PetDTO>> getAllPets() {
//        return ResponseEntity.ok(petService.getAllPets());
//    }
//
//    @GetMapping("/my-pets")
//    public ResponseEntity<List<PetDTO>> getAllPetsForCurrentUser() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        List<PetDTO> pets = petService.getAllPetsForUser(username);
//        return ResponseEntity.ok(pets);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
//        Pet pet = petService.getPetById(id);
//        PetDTO petDTO = petService.convertToDto(pet);
//        return ResponseEntity.ok(petDTO);
//    }
//
//    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<PetDTO> updatePet(@PathVariable Long id,
//                                            @RequestPart("petName") Optional<String> petName,
//                                            @RequestPart("age") Optional<Integer> age,
//                                            @RequestPart("weight") Optional<Double> weight,
//                                            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Pet pet = petService.getPetById(id);
//
//        // Update only fields that are provided
//        petName.ifPresent(pet::setPetName);
//        age.ifPresent(pet::setAge);
//        weight.ifPresent(pet::setWeight);
//
//        // Handle profile image
//        if (profileImage != null) {
//            Map<String, Object> uploadResult;
//            if (pet.getAvatarPublicId() != null && !pet.getAvatarPublicId().isEmpty()) {
//                uploadResult = uploadService.updateImage(pet.getAvatarPublicId(), profileImage);
//            } else {
//                uploadResult = uploadService.uploadImage(profileImage);
//            }
//            pet.setAvatarUrl((String) uploadResult.get("url"));
//            pet.setAvatarPublicId((String) uploadResult.get("public_id"));
//        }
//
//        Pet updatedPet = petService.updatePetForUser(id, pet, username);
//        PetDTO petDTO = petService.convertToDto(updatedPet);
//        return ResponseEntity.ok(petDTO);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        petService.deletePetForUser(id, username);
//        return ResponseEntity.noContent().build();
//    }
//}

package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponse;
import com.petconnect.backend.dto.PetDTO;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.services.PetService;
import com.petconnect.backend.services.UploadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final UploadService uploadService;

    @Autowired
    public PetController(UploadService uploadService, PetService petService) {
        this.uploadService = uploadService;
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PetDTO>> createPet(@Valid @RequestBody PetDTO petRequest,
                                                         @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String username = userDetails.getUsername();
            Pet createdPet = petService.createPetForUser(petRequest, username);
            PetDTO petDTO = petService.convertToDto(createdPet);
            ApiResponse<PetDTO> apiResponse = new ApiResponse<>("Pet created successfully", petDTO);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

        } catch (ResourceNotFoundException e) {
            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<PetDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

        @GetMapping
    public ResponseEntity<List<PetDTO>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/my-pets")
    public ResponseEntity<List<PetDTO>> getAllPetsForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PetDTO> pets = petService.getAllPetsForUser(username);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        Pet pet = petService.getPetById(id);
        PetDTO petDTO = petService.convertToDto(pet);
        return ResponseEntity.ok(petDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PetDTO>> updatePet(@PathVariable Long id,
                                                         @Valid @RequestBody PetDTO petRequest,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            Pet pet = petService.getPetById(id);

            // Update fields from the request DTO
            pet.setPetName(petRequest.getPetName());
            pet.setAge(petRequest.getAge());
            pet.setWeight(petRequest.getWeight());


            Pet updatedPet = petService.updatePetForUser(id, pet, username);
            PetDTO petDTO = petService.convertToDto(updatedPet);
            ApiResponse<PetDTO> apiResponse = new ApiResponse<>("Pet updated successfully", petDTO);
            return ResponseEntity.ok(apiResponse);

        } catch (ResourceNotFoundException e) {
            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<PetDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        petService.deletePetForUser(id, username);
        return ResponseEntity.noContent().build();
    }
}
