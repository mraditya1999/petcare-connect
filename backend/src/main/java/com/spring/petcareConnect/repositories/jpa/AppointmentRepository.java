package com.spring.petcareConnect.repositories.jpa;

import com.spring.petcareConnect.entities.Appointment;
import com.spring.petcareConnect.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Check if the SPECIALIST has an overlapping appointment
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.specialist.specialistId = :specialistId " +
            "AND a.appointmentStatus = 'SCHEDULED' " +
            "AND :start < FUNCTION('TIMESTAMPADD', MINUTE, a.duration, a.appointmentDate) " +
            "AND :end > a.appointmentDate")
    boolean isSpecialistBusy(@Param("specialistId") Long specialistId,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);

    // 2. Check if the PET has an overlapping appointment
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.pet.petId = :petId " +
            "AND a.appointmentStatus = 'SCHEDULED' " +
            "AND :start < FUNCTION('TIMESTAMPADD', MINUTE, a.duration, a.appointmentDate) " +
            "AND :end > a.appointmentDate")
    boolean isPetBusy(@Param("petId") Long petId,
                      @Param("start") LocalDateTime start,
                      @Param("end") LocalDateTime end);

    Optional<Appointment> findByAppointmentIdAndPetOwner(Long appointmentId, User petOwner);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :start AND :end AND a.appointmentStatus = 'SCHEDULED'")
    List<Appointment> findAppointmentsBetween(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

}