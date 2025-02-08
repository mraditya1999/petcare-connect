//package com.petconnect.backend.controllers;
//
//import com.petconnect.backend.dto.SpecialistDTO;
//import com.petconnect.backend.services.SpecialistService;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/specialists")
//public class SpecialistController {
//
//    private final SpecialistService service;
//
//    public SpecialistController(SpecialistService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public SpecialistDTO create(@RequestBody SpecialistDTO specialistDTO) {
//        return service.createSpecialist(specialistDTO);
//    }
//
//    @GetMapping
//    public List<SpecialistDTO> getAll() {
//        return service.getAllSpecialists();
//    }
//
//    @GetMapping("/{id}")
//    public SpecialistDTO getById(@PathVariable Long id) {
//        return service.getSpecialistById(id);
//    }
//
//    @PutMapping("/{id}")
//    public SpecialistDTO update(@PathVariable Long id, @RequestBody SpecialistDTO specialistDTO) {
//        return service.updateSpecialist(id, specialistDTO);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable Long id) {
//        service.deleteSpecialist(id);
//    }
//}package com.petconnect.backend.controllers;
//
//import com.petconnect.backend.dto.SpecialistDTO;
//import com.petconnect.backend.services.SpecialistService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/specialists")
//public class SpecialistController {
//
//    private final SpecialistService specialistService;
//
//    @Autowired
//    public SpecialistController(SpecialistService specialistService) {
//        this.specialistService = specialistService;
//    }
//
//    @PostMapping
//    public ResponseEntity<SpecialistDTO> createSpecialist(@RequestBody SpecialistDTO specialistDTO) {
//        SpecialistDTO created = specialistService.createSpecialist(specialistDTO);
//        return ResponseEntity.ok(created);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<SpecialistDTO>> getAllSpecialists() {
//        List<SpecialistDTO> specialists = specialistService.getAllSpecialists();
//        return ResponseEntity.ok(specialists);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SpecialistDTO> getSpecialistById(@PathVariable Long id) {
//        SpecialistDTO specialist = specialistService.getSpecialistById(id);
//        return ResponseEntity.ok(specialist);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<SpecialistDTO> updateSpecialist(@PathVariable Long id, @RequestBody SpecialistDTO specialistDTO) {
//        SpecialistDTO updated = specialistService.updateSpecialist(id, specialistDTO);
//        return ResponseEntity.ok(updated);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteSpecialist(@PathVariable Long id) {
//        specialistService.deleteSpecialist(id);
//        return ResponseEntity.noContent().build();
//    }
//}

package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.services.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private final SpecialistService specialistService;

    @Autowired
    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpecialistDTO> createSpecialist(@RequestBody SpecialistDTO specialistDTO) {
        SpecialistDTO created = specialistService.createSpecialist(specialistDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SpecialistDTO>> getAllSpecialists() {
        List<SpecialistDTO> specialists = specialistService.getAllSpecialists();
        return ResponseEntity.ok(specialists);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpecialistDTO> getSpecialistById(@PathVariable Long id) {
        SpecialistDTO specialist = specialistService.getSpecialistById(id);
        return ResponseEntity.ok(specialist);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpecialistDTO> updateSpecialist(@PathVariable Long id, @RequestBody SpecialistDTO specialistDTO) {
        SpecialistDTO updated = specialistService.updateSpecialist(id, specialistDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteSpecialist(@PathVariable Long id) {
        specialistService.deleteSpecialist(id);
        return ResponseEntity.noContent().build();
    }
}
