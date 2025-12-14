package com.petconnect.backend.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Long addressId;
    private Long pincode;
    private String city;
    private String state;
    private String country;
    private String locality;
}
