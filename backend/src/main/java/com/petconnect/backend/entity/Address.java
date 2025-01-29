package com.petconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @Column(nullable = false)
    private Integer pincode;

    private String city;
    private String state;
    private String country;
    private String locality;
}
