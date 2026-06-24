package com.spring.petcareConnect.config;

import com.spring.petcareConnect.entities.*;
import com.spring.petcareConnect.enums.RoleName;
import com.spring.petcareConnect.repositories.jpa.BreedRepository;
import com.spring.petcareConnect.repositories.jpa.RoleRepository;
import com.spring.petcareConnect.repositories.jpa.SpeciesRepository;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.repositories.jpa.SpecialistRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Initializes roles, example users (user/admin/specialist) and species+breeds.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final RoleRepository roleRepository;
    private final SpecialistRepository specialistRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           SpeciesRepository speciesRepository,
                           BreedRepository breedRepository,
                           RoleRepository roleRepository,
                           SpecialistRepository specialistRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
        this.roleRepository = roleRepository;
        this.specialistRepository = specialistRepository;
    }

    @Override
    public void run(String... args) {
        // Ensure roles exist
        Role adminRole = ensureRole(RoleName.ROLE_ADMIN);
        Role userRole = ensureRole(RoleName.ROLE_USER);
        Role specialistRole = ensureRole(RoleName.ROLE_SPECIALIST);

        // Create example users (user, admin, specialist)
        initializeUsers(userRole, adminRole, specialistRole);

        // Initialize species and breeds
        initializeSpeciesAndBreeds();
    }

    private Role ensureRole(RoleName name) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> roleRepository.save(new Role(null, name, new HashSet<>())));
    }

    private void initializeUsers(Role userRole, Role adminRole, Role specialistRole) {
        // Regular user
        if (userRepository.findByEmail("user1@example.com").isEmpty()) {
            User user1 = new User();
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.setEmail("user1@example.com");
            user1.setPassword(passwordEncoder.encode("Password@123"));
            user1.getRoles().add(userRole);
            user1.setVerified(true);
            user1.setProfileComplete(false);
            user1.setAccountLocked(false);
            userRepository.save(user1);
        }

        // Admin user
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.getRoles().add(adminRole);
            admin.setVerified(true);
            admin.setProfileComplete(true);
            userRepository.save(admin);
        }

        // Specialist user
        if (userRepository.findByEmail("specialist@example.com").isEmpty()) {
            User specialistUser = new User();
            specialistUser.setFirstName("Dr. Alice");
            specialistUser.setLastName("Vet");
            specialistUser.setEmail("specialist@example.com");
            specialistUser.setPassword(passwordEncoder.encode("Specialist@123"));
            specialistUser.getRoles().add(specialistRole);
            specialistUser.setVerified(true);
            specialistUser.setProfileComplete(true);
            // Save user first to satisfy FK on Specialist
            userRepository.save(specialistUser);

            // Create Specialist record and link to user
            Specialist specialist = new Specialist();
            specialist.setUser(specialistUser);
            specialist.setAbout("Experienced veterinary specialist available for appointments.");
            // appointments list will be empty by default
            specialistRepository.save(specialist);
        }
    }

    private void initializeSpeciesAndBreeds() {
        if (speciesRepository.count() > 0) {
            return; // already initialized
        }

        // Map of species name -> list of breed names
        Map<String, List<String>> data = new LinkedHashMap<>();
        data.put("Dog", Arrays.asList("Labrador Retriever", "German Shepherd", "Golden Retriever", "Bulldog", "Beagle"));
        data.put("Cat", Arrays.asList("Persian", "Siamese", "Maine Coon", "Bengal", "Sphynx"));
        data.put("Bird", Arrays.asList("Parrot", "Canary", "Cockatiel", "Lovebird", "Macaw"));
        data.put("Rabbit", Arrays.asList("Lop Rabbit", "Dutch Rabbit", "Angora Rabbit", "Rex Rabbit", "Lionhead Rabbit"));
        data.put("Fish", Arrays.asList("Goldfish", "Betta", "Guppy", "Molly", "Angelfish"));

        // Save species first and keep mapping to saved entity
        Map<String, Species> savedSpecies = new HashMap<>();
        for (String speciesName : data.keySet()) {
            Species s = new Species();
            s.setSpeciesName(speciesName);
            s.setBreeds(new HashSet<>());
            savedSpecies.put(speciesName, speciesRepository.save(s));
        }

        // Save breeds for each species
        List<Breed> breedsToSave = new ArrayList<>();
        data.forEach((speciesName, breedNames) -> {
            Species s = savedSpecies.get(speciesName);
            for (String breedName : breedNames) {
                Breed b = new Breed();
                b.setBreedName(breedName);
                b.setSpecies(s);
                breedsToSave.add(b);
            }
        });

        // Bulk save breeds - repository may save each and set ids
        breedRepository.saveAll(breedsToSave);
    }
}