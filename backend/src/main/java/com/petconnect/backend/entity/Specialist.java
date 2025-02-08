package com.petconnect.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a Specialist.
 */
@Entity@Table(name = "specialists")

public class Specialist extends User  {
    private String about;
    private String speciality;

    public Specialist() {
    }

    public Specialist(Long specialistId, String about, User user, String speciality) {
        this.about = about;
        this.speciality = speciality;
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

    @Override
    public String toString() {
        return "Specialist{" +
                ", about='" + about + '\'' +
                ", speciality='" + speciality + '\'' +
                '}';
    }
}