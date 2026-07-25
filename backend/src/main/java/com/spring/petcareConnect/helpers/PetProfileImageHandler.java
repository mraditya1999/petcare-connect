package com.spring.petcareConnect.helpers;

import com.spring.petcareConnect.entities.Pet;
import com.spring.petcareConnect.enums.ProfileType;
import com.spring.petcareConnect.services.UploadImageService;
import org.springframework.stereotype.Component;

@Component
public class PetProfileImageHandler extends ProfileImageHandler<Pet> {

    public PetProfileImageHandler(UploadImageService uploadImageService) {
        super(uploadImageService);
    }

    @Override
    protected ProfileType getProfileType() {
        return ProfileType.PET;
    }

    @Override
    protected String getAvatarPublicId(Pet entity) {
        return entity.getAvatarPublicId();
    }

    @Override
    protected void setAvatarUrl(Pet entity, String avatarUrl) {
        entity.setAvatarUrl(avatarUrl);
    }

    @Override
    protected void setAvatarPublicId(Pet entity, String publicId) {
        entity.setAvatarPublicId(publicId);
    }
}
