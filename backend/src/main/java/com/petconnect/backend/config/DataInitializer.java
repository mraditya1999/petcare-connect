package com.petconnect.backend.config;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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
        if (!userRepository.existsByEmail("ay5480620@gmail.com")) {
            createUser("Aditya","Yadav","ay5480620@gmail.com", "@mrAditya1999", Role.RoleName.ADMIN, true, false);
        }

//        if (!userRepository.existsByEmail("ay5480620@gmail.com")) {
//            createUser("Honey","Singh","dbadaditya@gmail.com", "@mrAditya1999", Role.RoleName.USER, true, true);
//        }
    }

    private void createUser(String firstName,String lastName,String email, String password, Role.RoleName role, boolean isVerified, boolean isAdmin) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerified(isVerified);
        user.setTwoFactorEnabled(false);
        user.setRoles(new HashSet<>(Set.of(roleRepository.findByRoleName(role).orElseThrow(() -> new IllegalArgumentException("Role not found")))));
        userRepository.save(user);
    }
}
