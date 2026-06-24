package com.spring.petcareConnect.helpers;

import com.spring.petcareConnect.dtos.upload.response.ImageUploadResponseDto;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.services.UploadImageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ProfileImageHandler {
    private final UploadImageService uploadImageService;

    public ProfileImageHandler(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    public void replace(User user, MultipartFile file) {
        delete(user);
        ImageUploadResponseDto imageInfo = uploadImageService.uploadProfileImage(
                file, UploadImageService.ProfileType.USER, null);
        user.setAvatarUrl(imageInfo.getUrl());
        user.setAvatarPublicId(imageInfo.getPublicId());
    }

    public void delete(User user) {
        if (user.getAvatarPublicId() != null) {
            uploadImageService.deleteProfileImage(user.getAvatarPublicId());
            user.setAvatarUrl(null);
            user.setAvatarPublicId(null);
        }
    }
}

