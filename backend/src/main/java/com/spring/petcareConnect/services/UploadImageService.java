package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.upload.response.ImageDataResponseDto;
import com.spring.petcareConnect.dtos.upload.response.ImageDeleteResponseDto;
import com.spring.petcareConnect.dtos.upload.response.ImageUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UploadImageService {
    enum ProfileType {
        USER, PET, SPECIALIST
    }

    String determineFolderPath(ProfileType profileType);

    ImageUploadResponseDto uploadProfileImage(MultipartFile file, ProfileType profileType, String publicId);

    ImageDeleteResponseDto deleteProfileImage(String publicId);

    public ImageDataResponseDto getImage(String publicId) throws Exception ;

}
