package com.petconnect.backend.config;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotFound(Role.RoleName.USER);
        createRoleIfNotFound(Role.RoleName.ADMIN);
        createRoleIfNotFound(Role.RoleName.SPECIALIST);
    }

    private void createRoleIfNotFound(Role.RoleName roleName) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }
    }
}
