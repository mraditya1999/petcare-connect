//package com.petconnect.backend.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
///**
// * Entity representing a Specialist.
// */
//@Entity@Table(name = "specialists")
//
//public class Specialist extends User  {
//    private String about;
//    private String speciality;
//
//    public Specialist() {
//    }
//
//    public Specialist(Long specialistId, String about, User user, String speciality) {
//        this.about = about;
//        this.speciality = speciality;
//    }
//
//    public String getAbout() {
//        return about;
//    }
//
//    public void setAbout(String about) {
//        this.about = about;
//    }
//
//
//    public String getSpeciality() {
//        return speciality;
//    }
//
//    public void setSpeciality(String speciality) {
//        this.speciality = speciality;
//    }
//
//    @Override
//    public String toString() {
//        return "Specialist{" +
//                ", about='" + about + '\'' +
//                ", speciality='" + speciality + '\'' +
//                '}';
//    }
//}
package com.petconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

/**
 * Entity representing a Specialist, extending User.
 */
@Entity
@Table(name = "specialists")
@PrimaryKeyJoinColumn(name = "specialist_id")
public class Specialist extends User {

    @Column(nullable = false, length = 500)
    private String about;

    @Column(nullable = false, length = 100)
    private String speciality;

    // Constructor initializing inherited User fields
    public Specialist(Long userId, String firstName, String lastName, String email, Address address,
                      String avatarUrl, String avatarPublicId, String mobileNumber, String password,
                      String verificationToken, String resetToken, Set<Role> roles, boolean isVerified,
                      String oauthProvider, String oauthProviderId, boolean isTwoFactorEnabled,
                      Date createdAt, Date updatedAt, String about, String speciality) {
        super(userId, firstName, lastName, email, address, avatarUrl, avatarPublicId, mobileNumber,
                password, verificationToken, resetToken, roles, isVerified, oauthProvider,
                oauthProviderId, isTwoFactorEnabled, createdAt, updatedAt);
        this.about = about;
        this.speciality = speciality;
    }

    @Override
    public String toString() {
        return "Specialist{" +
                "userId=" + getUserId() +  // Inherited userId
                ", firstName=" + getFirstName() +  // Inherited field
                ", lastName=" + getLastName() +
                ", email=" + getEmail() +
                ", about='" + about + '\'' +
                ", speciality='" + speciality + '\'' +
                '}';
    }

    public Specialist() {
    }

    public Specialist(Long userId, String firstName, String lastName, String email, Address address, String avatarUrl, String avatarPublicId, String mobileNumber, String password, String verificationToken, String resetToken, Set<Role> roles, boolean isVerified, String oauthProvider, String oauthProviderId, boolean isTwoFactorEnabled, Date createdAt, Date updatedAt) {
        super(userId, firstName, lastName, email, address, avatarUrl, avatarPublicId, mobileNumber, password, verificationToken, resetToken, roles, isVerified, oauthProvider, oauthProviderId, isTwoFactorEnabled, createdAt, updatedAt);
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
