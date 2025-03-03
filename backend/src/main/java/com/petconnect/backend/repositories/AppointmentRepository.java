package com.petconnect.backend.repositories;

import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.entity.Appointment.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find all appointments by pet owner with pagination
    Page<Appointment> findByPetOwnerUserId(Long petOwnerId, Pageable pageable);

    // Find all appointments by specialist
    Page<Appointment> findBySpecialistUserId(Long userId,Pageable pageable);

    // Find all appointments by pet with pagination
    Page<Appointment> findByPetPetId(Long petId, Pageable pageable);
    // Corrected method name

    // Find all appointments by status and user ID with pagination
    Page<Appointment> findByStatusAndPetOwnerUserId(AppointmentStatus status, Long userId, Pageable pageable);

    // Custom query to find all appointments between two dates
    @Query("SELECT a FROM Appointment a WHERE a.date BETWEEN :startDate AND :endDate")
    List<Appointment> findByDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);  // Corrected method name

    // Custom query to find all appointments for a specific specialist between two dates
    @Query("SELECT a FROM Appointment a WHERE a.specialist.userId = :userId AND a.date BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsBySpecialistAndBetweenDates(@Param("userId") Long userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Custom query to find all appointments for a specific pet owner between two dates
    @Query("SELECT a FROM Appointment a WHERE a.petOwner.userId = :petOwnerId AND a.date BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsByPetOwnerAndBetweenDates(@Param("petOwnerId") Long petOwnerId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Fetch available appointments for a specialist
    @Query("SELECT a FROM Appointment a WHERE a.specialist.userId = :userId AND a.status = 'AVAILABLE'")
    List<Appointment> findAvailableAppointmentsBySpecialist(@Param("userId") Long userId);
}
