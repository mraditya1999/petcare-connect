package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.PetRequestDTO;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController
{
    @Autowired
    private PetService petService;

    @Autowired
    UserRepository userRepository ;

//    @PostMapping("/create")
//    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
//        return ResponseEntity.ok(petService.createPet(pet));
//    }
@PostMapping("/create")
public ResponseEntity<Pet> createPet(@RequestBody PetRequestDTO petRequest) {
    User petOwner = userRepository.findById(petRequest.getPetOwnerId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    Pet pet = new Pet();
    pet.setPetName(petRequest.getPetName());
    pet.setAge(petRequest.getAge());
    pet.setWeight(petRequest.getWeight());
    pet.setAvatarUrl(petRequest.getAvatarUrl());
    pet.setPetOwner(petOwner);

    return ResponseEntity.ok(petService.createPet(pet));
}


    @GetMapping("/all")
    public ResponseEntity<List<Pet>> getAllPets()
    {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id)
    {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @RequestBody Pet pet)
    {
        return ResponseEntity.ok(petService.updatePet(id, pet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id)
    {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }


}
