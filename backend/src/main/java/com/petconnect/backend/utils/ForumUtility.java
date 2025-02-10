package com.petconnect.backend.utils;

import com.petconnect.backend.dto.ForumDTO;
import com.petconnect.backend.entity.Forum;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumUtility {

    private final UserRepository userRepository;

    @Autowired
    public ForumUtility(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUserDetailsToForumDTO(Forum forum, ForumDTO forumDTO) {
        User user = userRepository.findById(forum.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + forum.getUserId()));
        forumDTO.setFirstName(user.getFirstName());
        forumDTO.setLastName(user.getLastName());
        forumDTO.setEmail(user.getEmail());
    }
}

