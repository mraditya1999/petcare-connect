package com.spring.petcareConnect.helpers;

import com.spring.petcareConnect.dtos.upload.response.ImageUploadResponseDto;
import com.spring.petcareConnect.enums.ProfileType;
import com.spring.petcareConnect.services.UploadImageService;
import org.springframework.web.multipart.MultipartFile;

public abstract class ProfileImageHandler<T> {
    protected final UploadImageService uploadImageService;

    protected ProfileImageHandler(UploadImageService uploadImageService) {
        this.uploadImageService = uploadImageService;
    }

    protected abstract ProfileType getProfileType();

    protected abstract String getAvatarPublicId(T entity);

    protected abstract void setAvatarUrl(T entity, String avatarUrl);

    protected abstract void setAvatarPublicId(T entity, String publicId);

    public void create(T entity, MultipartFile file) {
        ImageUploadResponseDto imageInfo = uploadImageService.uploadProfileImage(file, getProfileType(), null);
        setAvatarUrl(entity, imageInfo.getUrl());
        setAvatarPublicId(entity, imageInfo.getPublicId());
    }

    public void replace(T entity, MultipartFile file) {
        delete(entity);
        create(entity, file);
    }

    public void delete(T entity) {
        String publicId = getAvatarPublicId(entity);
        if (publicId != null) {
            uploadImageService.deleteProfileImage(publicId);
            setAvatarUrl(entity, null);
            setAvatarPublicId(entity, null);
        }
    }
}
