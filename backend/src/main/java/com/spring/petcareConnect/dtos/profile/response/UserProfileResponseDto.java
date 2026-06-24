package com.spring.petcareConnect.dtos.profile.response;

import com.spring.petcareConnect.dtos.profile.request.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDto address;
    private String avatarUrl;
    private String avatarPublicId;
    private String mobileNumber;

}
