package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.UserDTO;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.dto.RoleDTO;
import java.util.stream.Collectors;

public class UserConverter {

    public static UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles().stream()
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setRoleId(role.getRoleId());
                    roleDTO.setRoleName(role.getRoleName());
                    return roleDTO;
                }).collect(Collectors.toSet()));

        return userDTO;
    }
}
