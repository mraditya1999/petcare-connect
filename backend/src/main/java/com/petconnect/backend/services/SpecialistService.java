//package com.petconnect.backend.services;
//
//import com.petconnect.backend.dto.SpecialistDTO;
//import com.petconnect.backend.entity.Role;
//import com.petconnect.backend.entity.Specialist;
//import com.petconnect.backend.mapper.SpecialistMapper;
//import com.petconnect.backend.repositories.RoleRepository;
//import com.petconnect.backend.repositories.SpecialistRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class SpecialistService {
//
//    private final SpecialistRepository repository;
//    private final PasswordEncoder passwordEncoder;
//    private final RoleRepository roleRepository;
//    private final SpecialistMapper mapper = SpecialistMapper.INSTANCE;
//
//    @Autowired
//    public SpecialistService(SpecialistRepository repository, SpecialistRepository specialistRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
//        this.repository = repository;
//        this.passwordEncoder = passwordEncoder;
//        this.roleRepository = roleRepository;
//    }
//
//    public Specialist createSpecialist(SpecialistDTO specialistDTO) {
//        Specialist specialist = mapper.toEntity(specialistDTO);
//        specialist.setPassword(passwordEncoder.encode(specialistDTO.getPassword()));
//        specialist.setRoles(new HashSet<>(Set.of(roleRepository.findByRoleName(Role.RoleName.SPECIALIST)
//                .orElseThrow(() -> new IllegalArgumentException("Role not found")))));
//        specialist.setVerified(true); // Assuming new specialists are verified by default
//
//        return repository.save(specialist);
//    }
//
//    public List<SpecialistDTO> getAllSpecialists() {
//        return repository.findAll().stream()
//                .map(mapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public SpecialistDTO getSpecialistById(Long id) {
//        Specialist specialist = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Specialist not found"));
//        return mapper.toDTO(specialist);
//    }
//
//    public SpecialistDTO updateSpecialist(Long id, SpecialistDTO specialistDTO) {
//        Specialist specialist = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Specialist not found"));
//        mapper.toEntity(specialistDTO);
//        specialist.setPassword(passwordEncoder.encode(specialistDTO.getPassword()));
//
//        Specialist updated = repository.save(specialist);
//        return mapper.toDTO(updated);
//    }
//
//    public void deleteSpecialist(Long id) {
//        repository.deleteById(id);
//    }
//}
