package com.petconnect.backend.services;

import com.petconnect.backend.dto.SpecialistDTO;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    private final SpecialistRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public SpecialistService(SpecialistRepository repository, SpecialistRepository specialistRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public Specialist createSpecialist(SpecialistDTO specialistDTO) {
        Specialist specialist = new Specialist();
        specialist.setFirstName(specialistDTO.getFirstName());
        specialist.setLastName(specialistDTO.getLastName());
        specialist.setEmail(specialistDTO.getEmail());
        specialist.setPassword(passwordEncoder.encode(specialistDTO.getPassword()));
        specialist.setSpeciality(specialistDTO.getSpeciality());
        specialist.setAbout(specialistDTO.getAbout());
        specialist.setAddress(specialistDTO.getAddress());
        specialist.setRoles(new HashSet<>(Set.of(roleRepository.findByRoleName(Role.RoleName.SPECIALIST)
                .orElseThrow(() -> new IllegalArgumentException("Role not found")))));
        specialist.setVerified(true); // Assuming new specialists are verified by default

        return repository.save(specialist);
    }

    public List<SpecialistDTO> getAllSpecialists() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SpecialistDTO getSpecialistById(Long id) {
        Specialist specialist = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        return convertToDTO(specialist);
    }

    public SpecialistDTO updateSpecialist(Long id, SpecialistDTO specialistDTO) {
        Specialist specialist = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        specialist.setFirstName(specialistDTO.getFirstName());
        specialist.setLastName(specialistDTO.getLastName());
        specialist.setEmail(specialistDTO.getEmail());
        specialist.setSpeciality(specialistDTO.getSpeciality());
        specialist.setAbout(specialistDTO.getAbout());

        // Encode the password before saving (if it's being updated)
        specialist.setPassword(passwordEncoder.encode(specialistDTO.getPassword()));

        Specialist updated = repository.save(specialist);
        return convertToDTO(updated);
    }

    public void deleteSpecialist(Long id) {
        repository.deleteById(id);
    }

    private SpecialistDTO convertToDTO(Specialist specialist) {
        return SpecialistDTO.builder()
                .id(specialist.getUserId())
                .firstName(specialist.getFirstName())
                .lastName(specialist.getLastName())
                .email(specialist.getEmail())
                .speciality(specialist.getSpeciality())
                .about(specialist.getAbout())
                .address(specialist.getAddress())
                .build();
    }
}
