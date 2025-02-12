//package com.petconnect.backend.controllers;
//
//import com.petconnect.backend.dto.ApiResponse;
//import com.petconnect.backend.dto.PetDTO;
//import com.petconnect.backend.exceptions.ResourceNotFoundException;
//import com.petconnect.backend.services.PetService;
//import com.petconnect.backend.services.UploadService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/pets")
//@Validated
//public class PetController {
//
//    private final PetService petService;
//    private final UploadService uploadService;
//
//    @Autowired
//    public PetController(UploadService uploadService, PetService petService) {
//        this.uploadService = uploadService;
//        this.petService = petService;
//    }
//
//    @PostMapping
//    public ResponseEntity<ApiResponse<PetDTO>> createPet(@Valid @RequestBody PetDTO petRequest,
//                                                         @AuthenticationPrincipal UserDetails userDetails) {
//
//        try {
//            String username = userDetails.getUsername();
//            PetDTO createdPetDTO = petService.createPetForUser(petRequest, username);
//            ApiResponse<PetDTO> apiResponse = new ApiResponse<>("Pet created successfully", createdPetDTO);
//            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
//
//        } catch (ResourceNotFoundException e) {
//            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (IllegalArgumentException e) {
//            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            ApiResponse<PetDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
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
//        PetDTO petDTO = petService.getPetById(id);
//        return ResponseEntity.ok(petDTO);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<PetDTO>> updatePet(@PathVariable Long id,
//                                                         @Valid @RequestBody PetDTO petRequest,
//                                                         @AuthenticationPrincipal UserDetails userDetails) {
//        try {
//            String username = userDetails.getUsername();
//            PetDTO updatedPetDTO = petService.updatePetForUser(id, petRequest, username);
//            ApiResponse<PetDTO> apiResponse = new ApiResponse<>("Pet updated successfully", updatedPetDTO);
//            return ResponseEntity.ok(apiResponse);
//
//        } catch (ResourceNotFoundException e) {
//            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (IllegalArgumentException e) {
//            ApiResponse<PetDTO> response = new ApiResponse<>(e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            ApiResponse<PetDTO> errorResponse = new ApiResponse<>("An error occurred: " + e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        petService.deletePetForUser(id, username);
//        return ResponseEntity.noContent().build();
//    }
//}
