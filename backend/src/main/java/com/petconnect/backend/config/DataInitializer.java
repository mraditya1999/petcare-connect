package com.petconnect.backend.config;

import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SpecialistRepository specialistRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, SpecialistRepository specialistRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.specialistRepository = specialistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound(Role.RoleName.USER);
        createRoleIfNotFound(Role.RoleName.ADMIN);
        createRoleIfNotFound(Role.RoleName.SPECIALIST);
        initializeDefaultUsers();
    }

    private void createRoleIfNotFound(Role.RoleName roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }
    }

    private void initializeDefaultUsers() {
        if (!userRepository.existsByEmail("admin@petconnect.com")) {
            createUser("Admin", "User", "admin@petconnect.com", "admin@123", Role.RoleName.ADMIN, true, createAddress("Headquarters", "Main City", "Country"));
        }
        if (!specialistRepository.existsByEmail("specialist@example.com")) {
            createSpecialist("John", "Doe", "specialist@example.com", "specialist123", Role.RoleName.SPECIALIST, true, "Veterinary Specialist", "Experienced veterinarian", createAddress("Animal Care Center", "Bengaluru", "India"));
        }

        // Additional users
        if (!userRepository.existsByEmail("alice.smith@example.com")) {
            createUser("Alice", "Smith", "alice.smith@example.com", "password1", Role.RoleName.USER, true, createAddress("Residential Area", "City A", "Country"));
        }
        if (!userRepository.existsByEmail("bob.johnson@example.com")) {
            createUser("Bob", "Johnson", "bob.johnson@example.com", "password2", Role.RoleName.USER, true, createAddress("Residential Area", "City B", "Country"));
        }
        if (!userRepository.existsByEmail("charlie.brown@example.com")) {
            createUser("Charlie", "Brown", "charlie.brown@example.com", "password3", Role.RoleName.USER, true, createAddress("Residential Area", "City C", "Country"));
        }
        if (!userRepository.existsByEmail("david.williams@example.com")) {
            createUser("David", "Williams", "david.williams@example.com", "password4", Role.RoleName.USER, true, createAddress("Residential Area", "City D", "Country"));
        }

        // Additional specialists
        if (!specialistRepository.existsByEmail("eve.davis@medical.com")) {
            createSpecialist("Eve", "Davis", "eve.davis@medical.com", "specialist123", Role.RoleName.SPECIALIST, true, "Cardiology Specialist", "Experienced in cardiology", createAddress("Heart Care Clinic", "City E", "Country"));
        }
        if (!specialistRepository.existsByEmail("frank.miller@neuro.com")) {
            createSpecialist("Frank", "Miller", "frank.miller@neuro.com", "specialist123", Role.RoleName.SPECIALIST, true, "Neurology Specialist", "Experienced in neurology", createAddress("Neuro Clinic", "City F", "Country"));
        }
        if (!specialistRepository.existsByEmail("grace.wilson@peds.com")) {
            createSpecialist("Grace", "Wilson", "grace.wilson@peds.com", "specialist123", Role.RoleName.SPECIALIST, true, "Pediatrics Specialist", "Experienced in pediatrics", createAddress("Children's Hospital", "City G", "Country"));
        }
        if (!specialistRepository.existsByEmail("henry.moore@derma.com")) {
            createSpecialist("Henry", "Moore", "henry.moore@derma.com", "specialist123", Role.RoleName.SPECIALIST, true, "Dermatology Specialist", "Experienced in dermatology", createAddress("Skin Care Center", "City H", "Country"));
        }
    }

    private Address createAddress(String locality, String city, String country) {
        Address address = new Address();
        address.setPincode(123456L);
        address.setCity(city);
        address.setState("State");
        address.setCountry(country);
        address.setLocality(locality);
        return address;
    }



    private void createUser(String firstName, String lastName, String email, String password, Role.RoleName role, boolean isVerified, Address address) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerified(isVerified);
        user.setRoles(new HashSet<>(Set.of(roleRepository.findByRoleName(role).orElseThrow(() -> new IllegalArgumentException("Role not found")))));
        user.setAddress(address);
        userRepository.save(user);
    }

    private void createSpecialist(String firstName, String lastName, String email, String password, Role.RoleName role, boolean isVerified, String specialty, String about, Address address) {
        Specialist specialist = new Specialist();
        specialist.setFirstName(firstName);
        specialist.setLastName(lastName);
        specialist.setEmail(email);
        specialist.setPassword(passwordEncoder.encode(password));
        specialist.setVerified(isVerified);
        specialist.setRoles(new HashSet<>(Set.of(roleRepository.findByRoleName(role).orElseThrow(() -> new IllegalArgumentException("Role not found")))));
        specialist.setSpeciality(specialty);
        specialist.setAbout(about);
        specialist.setAddress(address);
        specialistRepository.save(specialist);
    }

    private Address createAddress() {
        Address address = new Address();
        address.setPincode(123456L);
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setCountry("India");
        address.setLocality("Electronic City");
        return address;
    }
}
