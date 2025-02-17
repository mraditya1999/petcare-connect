package com.petconnect.backend.config;

import com.petconnect.backend.entity.Address;
import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
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
//        initializeDefaultUsers();
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
            createUser("Aditya", "Yadav", "ay5480620@gmail.com", "@mrAditya1999", Role.RoleName.ADMIN, true, createAddress());
        }
//
//        if (!userRepository.existsByEmail("dbadaditya@gmail.com")) {
//            createUser("Honey", "Singh", "dbadaditya@gmail.com", "@mrAditya1999", Role.RoleName.USER, true ,createAddress());
//        }
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
