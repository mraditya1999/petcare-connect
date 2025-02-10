package com.petconnect.backend.services;

import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.repositories.AppointmentRepository;
import com.petconnect.backend.repositories.PetRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppointmentService
{
    @Autowired
    private AppointmentRepository appointmentRepository;
    private PetRepository petRepository;
    private SpecialistRepository specialistRepository;
    private UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PetRepository petRepository,
            SpecialistRepository specialistRepository,
            UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
    }

    public Appointment createAppointment(Long petOwnerId, Long petId, Long specialistId, Date date) {
        User petOwner = userRepository.findById(petOwnerId)
                .orElseThrow(() -> new RuntimeException("Pet Owner not found"));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        Appointment appointment = new Appointment();
        appointment.setPetOwner(petOwner);
        appointment.setPet(pet);
        appointment.setSpecialist(specialist);
        appointment.setDate(date);

        return appointmentRepository.save(appointment);
    }
}
