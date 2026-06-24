package com.spring.petcareConnect.dtos.upload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDeleteResponseDto {
    private String publicId;
    private boolean deleted;
    private String message;
}

