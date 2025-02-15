package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.PetDTO;
import com.petconnect.backend.services.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<PetDTO> createPetForUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name,
            @RequestParam("breed") String breed,
            @RequestParam("age") int age,
            @RequestParam("weight") Double weight,
            @RequestParam("gender") String gender,
            @RequestParam("species") String species,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
            ) throws IOException {

        String username = userDetails.getUsername();

        PetDTO petDTO = new PetDTO(name, breed, age, weight, gender, species);
        PetDTO createdPet = petService.createPetForUser(petDTO, avatarFile,username);
        logger.info("Pet created for user: {}", userDetails.getUsername());
        return ResponseEntity.ok(createdPet);
    }

    @GetMapping
    public ResponseEntity<List<PetDTO>> getAllPetsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        List<PetDTO> pets = petService.getAllPetsForUser();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetOfUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PetDTO pet = petService.getPetOfUserById(id);
        return ResponseEntity.ok(pet);
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<PetDTO> updatePetForUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "breed", required = false) String breed,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "species", required = false) String species,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) throws IOException {

        PetDTO petDTO = new PetDTO(name, breed, age, weight, gender, species);
        PetDTO updatedPet = petService.updatePetForUser(id, petDTO, avatarFile);
        logger.info("Pet updated for user: {}", userDetails.getUsername());
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePetForUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        petService.deletePetForUser(id,userDetails);
        logger.info("Pet deleted with ID: {}", id);
        return ResponseEntity.ok("Pet successfully deleted.");
    }
}
