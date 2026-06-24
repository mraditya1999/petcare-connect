package com.spring.petcareConnect.listeners;

import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.services.UploadImageService;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEntityListener {

    private final UploadImageService uploadImageService;

    @Autowired
    public UserEntityListener(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    @PreRemove
    public void preRemove(User user) {
        if (user.getAvatarPublicId() != null && !user.getAvatarPublicId().isBlank()) {
            uploadImageService.deleteProfileImage(user.getAvatarPublicId());
        }
    }
}

