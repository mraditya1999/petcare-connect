package com.spring.petcareConnect.dtos.upload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponseDto {
    private String publicId;
    private String url;
    private String folderPath;
    private long size;
    private String format;
}
