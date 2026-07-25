package com.spring.petcareConnect.helpers;

import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.ProfileType;
import com.spring.petcareConnect.services.UploadImageService;
import org.springframework.stereotype.Component;

@Component
public class UserProfileImageHandler extends ProfileImageHandler<User> {

    public UserProfileImageHandler(UploadImageService uploadImageService) {
        super(uploadImageService);
    }

    @Override
    protected ProfileType getProfileType() {
        return ProfileType.USER;
    }

    @Override
    protected String getAvatarPublicId(User entity) {
        return entity.getAvatarPublicId();
    }

    @Override
    protected void setAvatarUrl(User entity, String avatarUrl) {
        entity.setAvatarUrl(avatarUrl);
    }

    @Override
    protected void setAvatarPublicId(User entity, String publicId) {
        entity.setAvatarPublicId(publicId);
    }
}

