package com.petconnect.backend.services;

import com.petconnect.backend.dto.appointment.AppointmentRequestDTO;
import com.petconnect.backend.dto.appointment.AppointmentResponseDTO;
import com.petconnect.backend.dto.appointment.AppointmentUpdateRequestDTO;
import com.petconnect.backend.dto.appointment.FeedbackDTO;
import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.entity.Appointment.AppointmentStatus;
import com.petconnect.backend.entity.Pet;
import com.petconnect.backend.entity.Specialist;
import com.petconnect.backend.entity.User;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UnauthorizedAccessException;
import com.petconnect.backend.mappers.AppointmentMapper;
import com.petconnect.backend.repositories.AppointmentRepository;
import com.petconnect.backend.repositories.PetRepository;
import com.petconnect.backend.repositories.SpecialistRepository;
import com.petconnect.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final SpecialistRepository specialistRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper, UserRepository userRepository, PetRepository petRepository, SpecialistRepository specialistRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.specialistRepository = specialistRepository;
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByPetOwner(Long petOwnerId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return appointmentRepository.findByPetOwnerUserId(petOwnerId, pageable)
                    .map(appointmentMapper::toAppointmentResponseDTO);
        } catch (Exception e) {
            logger.error("Error fetching appointments for pet owner with ID {}: {}", petOwnerId, e.getMessage(), e);
            throw new RuntimeException("Error fetching appointments", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByPet(Long petId, int page, int size, Long userId) {
        try {
            // Verify that the authenticated user has permissions to access the pet's appointments
            Pet pet = petRepository.findById(petId).orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
            if (!pet.getPetOwner().getUserId().equals(userId)) {
                throw new UnauthorizedAccessException("User does not have permission to access this pet's appointments");
            }

            Pageable pageable = PageRequest.of(page, size);
            return appointmentRepository.findByPetPetId(petId, pageable)
                    .map(appointmentMapper::toAppointmentResponseDTO);
        } catch (Exception e) {
            logger.error("Error fetching appointments for pet with ID {}: {}", petId, e.getMessage(), e);
            throw new RuntimeException("Error fetching appointments", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByStatusForUser(AppointmentStatus status, int page, int size, Long userId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return appointmentRepository.findByStatusAndPetOwnerUserId(status, userId, pageable)
                    .map(appointmentMapper::toAppointmentResponseDTO);
        } catch (Exception e) {
            logger.error("Error fetching appointments by status {} for user with ID {}: {}", status, userId, e.getMessage(), e);
            throw new RuntimeException("Error fetching appointments", e);
        }
    }

    @Transactional
    public AppointmentResponseDTO createAppointment(Long userId, AppointmentRequestDTO appointmentRequestDTO) {
        try {
            // Validate and set entities
            Pet pet = petRepository.findById(appointmentRequestDTO.getPetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + appointmentRequestDTO.getPetId()));

            Specialist specialist = specialistRepository.findById(appointmentRequestDTO.getSpecialistId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + appointmentRequestDTO.getSpecialistId()));

            User petOwner = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Convert DTO to entity
            Appointment appointment = new Appointment();
            appointment.setPet(pet);
            appointment.setSpecialist(specialist);
            appointment.setPetOwner(petOwner);
            appointment.setDate(appointmentRequestDTO.getDate());
            appointment.setNotes(appointmentRequestDTO.getNotes());
            appointment.setDuration(appointmentRequestDTO.getDuration());
            appointment.setStatus(AppointmentStatus.SCHEDULED);

            // Save and return the response DTO
            Appointment savedAppointment = appointmentRepository.save(appointment);
            return appointmentMapper.toAppointmentResponseDTO(savedAppointment);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error creating appointment: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating appointment", e);
        }
    }

    @Transactional
    public AppointmentResponseDTO updateAppointment(Long appointmentId, Long userId, AppointmentUpdateRequestDTO updatedAppointmentDTO) {
        try {
            Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

            // Check if the user owns the appointment
            if (!existingAppointment.getPetOwner().getUserId().equals(userId)) {
                throw new UnauthorizedAccessException("User does not have permission to update this appointment");
            }

            // Update appointment details
            existingAppointment.setDate(updatedAppointmentDTO.getDate());
            existingAppointment.setNotes(updatedAppointmentDTO.getNotes());
            existingAppointment.setDuration(updatedAppointmentDTO.getDuration());
            existingAppointment.setStatus(updatedAppointmentDTO.getStatus()); // Update status

            // Save and return the response DTO
            Appointment savedAppointment = appointmentRepository.save(existingAppointment);
            return appointmentMapper.toAppointmentResponseDTO(savedAppointment);
        } catch (ResourceNotFoundException e) {
            logger.error("Appointment not found: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating appointment: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating appointment", e);
        }
    }

    @Transactional
    public void submitFeedback(Long userId, Long appointmentId, FeedbackDTO feedbackDTO) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

            // Ensure the user is the pet owner
            if (!appointment.getPetOwner().getUserId().equals(userId)) {
                throw new RuntimeException("User does not have permission to submit feedback for this appointment.");
            }

            // Add feedback and rating
            appointment.setFeedback(feedbackDTO.getFeedback());
            appointment.setRating(feedbackDTO.getRating());

            appointmentRepository.save(appointment);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error submitting feedback: {}", e.getMessage(), e);
            throw new RuntimeException("Error submitting feedback", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentHistory(Long userId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return appointmentRepository.findByPetOwnerUserId(userId, pageable)
                    .map(appointmentMapper::toAppointmentResponseDTO);
        } catch (Exception e) {
            logger.error("Error fetching appointment history for user with ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error fetching appointment history", e);
        }
    }

//    FOR SPECIALISTS
@Transactional(readOnly = true)
public Page<AppointmentResponseDTO> getAppointmentsForSpecialist(Long specialistId, int page, int size) {
    try {
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findBySpecialistUserId(specialistId, pageable)
                .map(appointmentMapper::toAppointmentResponseDTO);
    } catch (Exception e) {
        logger.error("Error fetching appointments for specialist with ID {}: {}", specialistId, e.getMessage(), e);
        throw new RuntimeException("Error fetching appointments for specialist", e);
    }
}


}
