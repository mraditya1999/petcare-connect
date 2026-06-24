package com.spring.petcareConnect.listeners;

import com.spring.petcareConnect.entities.Pet;
import com.spring.petcareConnect.services.UploadImageService;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PetEntityListener {

    private final UploadImageService uploadImageService;

    @Autowired
    public PetEntityListener(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    @PreRemove
    public void preRemove(Pet pet) {
        if (pet.getAvatarPublicId() != null && !pet.getAvatarPublicId().isBlank()) {
            uploadImageService.deleteProfileImage(pet.getAvatarPublicId());
        }
    }
}
