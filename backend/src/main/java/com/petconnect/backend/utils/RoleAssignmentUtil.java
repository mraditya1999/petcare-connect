package com.petconnect.backend.utils;

import com.petconnect.backend.entity.Role;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.RoleRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class RoleAssignmentUtil {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleAssignmentUtil(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public void assignRoles(User user, Role.RoleName defaultRole, Role.RoleName additionalRole) {
        boolean isFirstUser = userRepository.count() == 0;
        Set<Role> roles = new HashSet<>();

        roles.add(fetchRole(defaultRole));

        if (isFirstUser && additionalRole != null) {
            roles.add(fetchRole(additionalRole));
        }

        user.setRoles(roles);
    }

    public void assignRoles(User user, Set<Role.RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (Role.RoleName roleName : roleNames) {
            roles.add(fetchRole(roleName));
        }
        user.setRoles(roles);
    }

    private Role fetchRole(Role.RoleName roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " role not found"));
    }
}
