//package com.petconnect.backend.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//public class Address {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer addressId;
//
//    @Column(nullable = false)
//    private Integer pincode;
//
//    private String city;
//    private String state;
//    private String country;
//    private String locality;
//
//    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private User user;
//
//    public Address() {
//    }
//
//    public Address(Integer addressId, Integer pincode, String city, String state, String country, String locality, User user) {
//        this.addressId = addressId;
//        this.pincode = pincode;
//        this.city = city;
//        this.state = state;
//        this.country = country;
//        this.locality = locality;
//        this.user = user;
//    }
//
//    public Integer getAddressId() {
//        return addressId;
//    }
//
//    public void setAddressId(Integer addressId) {
//        this.addressId = addressId;
//    }
//
//    public Integer getPincode() {
//        return pincode;
//    }
//
//    public void setPincode(Integer pincode) {
//        this.pincode = pincode;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public String getLocality() {
//        return locality;
//    }
//
//    public void setLocality(String locality) {
//        this.locality = locality;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//}
package com.petconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(nullable = false)
    private Long pincode;

    private String city;
    private String state;
    private String country;
    private String locality;

    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private User user;

    @Version
    private Long version;

    public Address() {
    }

    public Address(Long addressId, Long pincode, String city, String state, String country, String locality, User user) {
        this.addressId = addressId;
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.country = country;
        this.locality = locality;
        this.user = user;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getPincode() {
        return pincode;
    }

    public void setPincode(Long pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
