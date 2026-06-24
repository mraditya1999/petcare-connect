package com.spring.petcareConnect.dtos.upload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDataResponseDto {
    private String publicId;
    private String url;
    private String folderPath;
    private int width;
    private int height;
    private String format;
}
