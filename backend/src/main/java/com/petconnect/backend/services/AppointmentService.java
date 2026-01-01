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
import com.petconnect.backend.repositories.jpa.AppointmentRepository;
import com.petconnect.backend.repositories.jpa.PetRepository;
import com.petconnect.backend.repositories.jpa.SpecialistRepository;
import com.petconnect.backend.repositories.jpa.UserRepository;
import com.petconnect.backend.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final SpecialistRepository specialistRepository;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByPetOwner(Long petOwnerId, int page, int size) {
        Pageable pageable = PaginationUtils.createPageable(page, size);
        return appointmentRepository.findByPetOwnerUserId(petOwnerId, pageable)
                .map(appointmentMapper::toAppointmentResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByPetOwner(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return getAppointmentsByPetOwner(user.getUserId(), page, size);
    }

    /**
     * Gets appointments for a specific pet, verifying user ownership.
     *
     * @param petId the pet ID (must not be null)
     * @param page the page number (must be non-negative)
     * @param size the page size (must be positive)
     * @param userId the user ID (must not be null)
     * @return a page of appointment response DTOs
     * @throws IllegalArgumentException if any parameter is invalid
     * @throws ResourceNotFoundException if pet is not found
     * @throws UnauthorizedAccessException if user does not own the pet
     */
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByPet(Long petId, int page, int size, Long userId) {
        if (petId == null) {
            throw new IllegalArgumentException("Pet ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        
        try {
            // Verify that the authenticated user has permissions to access the pet's appointments
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
            if (pet.getPetOwner() == null || !pet.getPetOwner().getUserId().equals(userId)) {
                logger.warn("User {} attempted to access appointments for pet {} not owned by them", userId, petId);
                throw new UnauthorizedAccessException("User does not have permission to access this pet's appointments");
            }

            Pageable pageable = PaginationUtils.createPageable(page, size);
            return appointmentRepository.findByPetPetId(petId, pageable)
                    .map(appointmentMapper::toAppointmentResponseDTO);
        } catch (ResourceNotFoundException | UnauthorizedAccessException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving appointments for pet {} by user {}", petId, userId, e);
            throw new RuntimeException("Failed to retrieve appointments", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByPet(Long petId, int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return getAppointmentsByPet(petId, page, size, user.getUserId());
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByStatusForUser(AppointmentStatus status, int page, int size, Long userId) {
        Pageable pageable = PaginationUtils.createPageable(page, size);
        return appointmentRepository.findByStatusAndPetOwnerUserId(status, userId, pageable)
                .map(appointmentMapper::toAppointmentResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsByStatusForUser(AppointmentStatus status, int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return getAppointmentsByStatusForUser(status, page, size, user.getUserId());
    }

    /**
     * Creates a new appointment.
     *
     * @param userId the user ID creating the appointment (must not be null)
     * @param appointmentRequestDTO the appointment request data (must not be null)
     * @return the created appointment response DTO
     * @throws IllegalArgumentException if userId is null or appointmentRequestDTO is null/invalid
     * @throws ResourceNotFoundException if pet, specialist, or user is not found
     */
    @Transactional
    public AppointmentResponseDTO createAppointment(Long userId, AppointmentRequestDTO appointmentRequestDTO) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (appointmentRequestDTO == null) {
            throw new IllegalArgumentException("AppointmentRequestDTO cannot be null");
        }
        if (appointmentRequestDTO.getPetId() == null) {
            throw new IllegalArgumentException("Pet ID cannot be null");
        }
        if (appointmentRequestDTO.getSpecialistId() == null) {
            throw new IllegalArgumentException("Specialist ID cannot be null");
        }
        if (appointmentRequestDTO.getDate() == null) {
            throw new IllegalArgumentException("Appointment date cannot be null");
        }
        if (appointmentRequestDTO.getDuration() <= 0) {
            throw new IllegalArgumentException("Appointment duration must be positive");
        }
        
        try {
            // Validate and set entities
            Pet pet = petRepository.findById(appointmentRequestDTO.getPetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + appointmentRequestDTO.getPetId()));

            Specialist specialist = specialistRepository.findById(appointmentRequestDTO.getSpecialistId())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with ID: " + appointmentRequestDTO.getSpecialistId()));

            User petOwner = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Verify pet belongs to the user
            if (!pet.getPetOwner().getUserId().equals(userId)) {
                logger.warn("User {} attempted to create appointment for pet {} not owned by them", userId, appointmentRequestDTO.getPetId());
                throw new IllegalArgumentException("Pet does not belong to the user");
            }

            // Convert DTO to entity
            Appointment appointment = new Appointment();
            appointment.setPet(pet);
            appointment.setSpecialist(specialist);
            appointment.setPetOwner(petOwner);
            appointment.setDate(appointmentRequestDTO.getDate());
            appointment.setNotes(appointmentRequestDTO.getNotes() != null ? appointmentRequestDTO.getNotes().trim() : null);
            appointment.setDuration(appointmentRequestDTO.getDuration());
            appointment.setStatus(AppointmentStatus.SCHEDULED);

            // Save and return the response DTO
            Appointment savedAppointment = appointmentRepository.save(appointment);
            logger.info("Appointment created with ID: {} for user: {}", savedAppointment.getAppointmentId(), userId);
            return appointmentMapper.toAppointmentResponseDTO(savedAppointment);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating appointment for user: {}", userId, e);
            throw new RuntimeException("Failed to create appointment", e);
        }
    }

    @Transactional
    public AppointmentResponseDTO createAppointment(String email, AppointmentRequestDTO appointmentRequestDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return createAppointment(user.getUserId(), appointmentRequestDTO);
    }

    /**
     * Updates an appointment if the user is the owner.
     *
     * @param appointmentId the appointment ID (must not be null)
     * @param userId the user ID (must not be null)
     * @param updatedAppointmentDTO the update data (must not be null)
     * @return the updated appointment response DTO
     * @throws IllegalArgumentException if any parameter is null or invalid
     * @throws ResourceNotFoundException if appointment is not found
     * @throws UnauthorizedAccessException if user is not the owner
     */
    @Transactional
    public AppointmentResponseDTO updateAppointment(Long appointmentId, Long userId, AppointmentUpdateRequestDTO updatedAppointmentDTO) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (updatedAppointmentDTO == null) {
            throw new IllegalArgumentException("AppointmentUpdateRequestDTO cannot be null");
        }
        
        try {
            Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

            // Check if the user owns the appointment
            if (existingAppointment.getPetOwner() == null || !existingAppointment.getPetOwner().getUserId().equals(userId)) {
                logger.warn("User {} attempted to update appointment {} not owned by them", userId, appointmentId);
                throw new UnauthorizedAccessException("User does not have permission to update this appointment");
            }

            // Update appointment details
            if (updatedAppointmentDTO.getDate() != null) {
                existingAppointment.setDate(updatedAppointmentDTO.getDate());
            }
            if (updatedAppointmentDTO.getNotes() != null) {
                existingAppointment.setNotes(updatedAppointmentDTO.getNotes().trim());
            }
            if (updatedAppointmentDTO.getDuration() != null) {
                if (updatedAppointmentDTO.getDuration() <= 0) {
                    throw new IllegalArgumentException("Appointment duration must be positive");
                }
                existingAppointment.setDuration(updatedAppointmentDTO.getDuration());
            }
            if (updatedAppointmentDTO.getStatus() != null) {
                existingAppointment.setStatus(updatedAppointmentDTO.getStatus());
            }

            // Save and return the response DTO
            Appointment savedAppointment = appointmentRepository.save(existingAppointment);
            logger.info("Appointment {} updated by user {}", appointmentId, userId);
            return appointmentMapper.toAppointmentResponseDTO(savedAppointment);
        } catch (ResourceNotFoundException | UnauthorizedAccessException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating appointment {} for user {}", appointmentId, userId, e);
            throw new RuntimeException("Failed to update appointment", e);
        }
    }

    @Transactional
    public AppointmentResponseDTO updateAppointment(Long appointmentId, String email, AppointmentUpdateRequestDTO updatedAppointmentDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return updateAppointment(appointmentId, user.getUserId(), updatedAppointmentDTO);
    }

    /**
     * Submits feedback for an appointment.
     *
     * @param userId the user ID (must not be null)
     * @param appointmentId the appointment ID (must not be null)
     * @param feedbackDTO the feedback data (must not be null)
     * @throws IllegalArgumentException if any parameter is null or invalid
     * @throws ResourceNotFoundException if appointment is not found
     * @throws UnauthorizedAccessException if user is not the pet owner
     */
    @Transactional
    public void submitFeedback(Long userId, Long appointmentId, FeedbackDTO feedbackDTO) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        if (feedbackDTO == null) {
            throw new IllegalArgumentException("FeedbackDTO cannot be null");
        }
        if (feedbackDTO.getRating() != null && (feedbackDTO.getRating() < 1 || feedbackDTO.getRating() > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

            // Ensure the user is the pet owner
            if (appointment.getPetOwner() == null || !appointment.getPetOwner().getUserId().equals(userId)) {
                logger.warn("User {} attempted to submit feedback for appointment {} not owned by them", userId, appointmentId);
                throw new UnauthorizedAccessException("User does not have permission to submit feedback for this appointment.");
            }

            // Add feedback and rating
            if (feedbackDTO.getFeedback() != null) {
                appointment.setFeedback(feedbackDTO.getFeedback().trim());
            }
            if (feedbackDTO.getRating() != null) {
                appointment.setRating(feedbackDTO.getRating());
            }

            appointmentRepository.save(appointment);
            logger.info("Feedback submitted for appointment {} by user {}", appointmentId, userId);
        } catch (ResourceNotFoundException | UnauthorizedAccessException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error submitting feedback for appointment {} by user {}", appointmentId, userId, e);
            throw new RuntimeException("Failed to submit feedback", e);
        }
    }

    @Transactional
    public void submitFeedback(String email, Long appointmentId, FeedbackDTO feedbackDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        submitFeedback(user.getUserId(), appointmentId, feedbackDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentHistory(Long userId, int page, int size) {
        Pageable pageable = PaginationUtils.createPageable(page, size);
        return appointmentRepository.findByPetOwnerUserId(userId, pageable)
                .map(appointmentMapper::toAppointmentResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentHistory(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return getAppointmentHistory(user.getUserId(), page, size);
    }

    //    FOR SPECIALISTS
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsForSpecialist(Long specialistId, int page, int size) {
        Pageable pageable = PaginationUtils.createPageable(page, size);
        return appointmentRepository.findBySpecialistUserId(specialistId, pageable)
                .map(appointmentMapper::toAppointmentResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointmentsForSpecialist(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + email));
        return getAppointmentsForSpecialist(user.getUserId(), page, size);
    }


}
