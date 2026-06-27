package com.spring.petcareConnect.helpers;

import com.spring.petcareConnect.dtos.upload.response.ImageUploadResponseDto;
import com.spring.petcareConnect.entities.Pet;
import com.spring.petcareConnect.enums.ProfileType;
import com.spring.petcareConnect.services.UploadImageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PetProfileImageHandler {
    private final UploadImageService uploadImageService;

    public PetProfileImageHandler(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    public void create(Pet pet, MultipartFile file) {
        ImageUploadResponseDto imageInfo = uploadImageService.uploadProfileImage(
                file, ProfileType.PET, null);
        pet.setAvatarUrl(imageInfo.getUrl());
        pet.setAvatarPublicId(imageInfo.getPublicId());
    }

    public void replace(Pet pet, MultipartFile file) {
        delete(pet);
        ImageUploadResponseDto imageInfo = uploadImageService.uploadProfileImage(
                file, ProfileType.PET, null);
        pet.setAvatarUrl(imageInfo.getUrl());
        pet.setAvatarPublicId(imageInfo.getPublicId());
    }

    public void delete(Pet pet) {
        if (pet.getAvatarPublicId() != null) {
            uploadImageService.deleteProfileImage(pet.getAvatarPublicId());
            pet.setAvatarUrl(null);
            pet.setAvatarPublicId(null);
        }
    }
}
