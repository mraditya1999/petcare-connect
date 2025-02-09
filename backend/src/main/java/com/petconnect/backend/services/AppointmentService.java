package com.petconnect.backend.services;

import com.petconnect.backend.dto.AppointmentDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
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


    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        // Validate pet
        Optional<Pet> petOptional = petRepository.findById(appointmentDTO.getPetId());
        if (petOptional.isEmpty()) {
            throw new RuntimeException("Pet not found with ID: " + appointmentDTO.getPetId());
        }

        // Validate specialist
        Optional<Specialist> specialistOptional = specialistRepository.findById(appointmentDTO.getSpecialistId());
        if (specialistOptional.isEmpty()) {
            throw new RuntimeException("Specialist not found with ID: " + appointmentDTO.getSpecialistId());
        }

        // Validate pet owner (user)
        Optional<User> userOptional = userRepository.findById(appointmentDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + appointmentDTO.getUserId());
        }

        // Convert DTO to Entity
        Appointment appointment = new Appointment();
        appointment.setDate(appointmentDTO.getDate());
        appointment.setPet(petOptional.get());
        appointment.setSpecialist(specialistOptional.get());
        appointment.setPetOwner(userOptional.get());
        appointment.setCreatedAt(new Date()); // Set creation time

        // Save to DB
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Convert Entity to DTO and return
        return new AppointmentDTO(
                savedAppointment.getAppointmentId(),
                savedAppointment.getDate(),
                savedAppointment.getPet().getPetId(),
                savedAppointment.getSpecialist().getSpecialistId(),
                savedAppointment.getPetOwner().getUserId()
        );
    }

//    public AppointmentDTO getAppointmentById(Long id) {
//        Appointment appointment = appointmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//        return new AppointmentDTO(appointment);
//    }



    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointment -> new AppointmentDTO(
                        appointment.getAppointmentId(),
                        appointment.getDate(),
                        appointment.getPet().getPetId(),
                        appointment.getSpecialist().getSpecialistId(),
                        appointment.getPetOwner().getUserId()
                )).toList();
    }

    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointment -> new AppointmentDTO(
                        appointment.getAppointmentId(),
                        appointment.getDate(),
                        appointment.getPet().getPetId(),
                        appointment.getSpecialist().getSpecialistId(),
                        appointment.getPetOwner().getUserId()
                ))
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }



    public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
        System.out.println("Updating Appointment ID: " + id);
        System.out.println("Incoming Data: " + appointmentDTO);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Pet pet = petRepository.findById(appointmentDTO.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        Specialist specialist = specialistRepository.findById(appointmentDTO.getSpecialistId())
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        User user = userRepository.findById(appointmentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Found Entities: Pet ID: " + pet.getPetId() + ", Specialist ID: " + specialist.getSpecialistId() + ", User ID: " + user.getUserId());

        appointment.setDate(appointmentDTO.getDate());
        appointment.setPet(pet);
        appointment.setSpecialist(specialist);
        appointment.setPetOwner(user);

        appointment = appointmentRepository.save(appointment);
        System.out.println("Updated Appointment: " + appointment);

        return new AppointmentDTO(appointment);
    }


    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }

}
