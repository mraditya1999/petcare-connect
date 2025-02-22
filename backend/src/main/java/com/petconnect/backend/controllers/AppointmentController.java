package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.appointment.AppointmentUpdateRequestDTO;
import com.petconnect.backend.dto.appointment.FeedbackDTO;
import com.petconnect.backend.dto.appointment.AppointmentRequestDTO;
import com.petconnect.backend.dto.appointment.AppointmentResponseDTO;
import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.AppointmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public AppointmentController(AppointmentService appointmentService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/petOwner")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByPetOwner(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String username = userDetails.getUsername();
            Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPetOwner(userId, page, size);
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments for pet owner successfully", appointments));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found while fetching appointments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error fetching appointments for pet owner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error fetching appointments: " + e.getMessage(), null));
        }
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found")).getUserId();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPet(petId, page, size, userId);
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments for pet successfully", appointments));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error fetching appointments for pet with ID {}: {}", petId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error fetching appointments: " + e.getMessage(), null));
        }
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByStatus(
            @PathVariable Appointment.AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found")).getUserId();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStatusForUser(status, page, size, userId);
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments by status successfully", appointments));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error fetching appointments by status {}: {}", status, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error fetching appointments: " + e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> createAppointment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO) {
        try {
            String username = userDetails.getUsername();
            Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
            AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(userId, appointmentRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>("Appointment created successfully", createdAppointment));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found while creating appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error creating appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error creating appointment: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updateAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId,
            @RequestBody @Valid AppointmentUpdateRequestDTO updatedAppointmentDTO) {
        try {
            String username = userDetails.getUsername();
            Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
            AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(appointmentId, userId, updatedAppointmentDTO);
            return ResponseEntity.ok(new ApiResponseDTO<>("Appointment updated successfully", updatedAppointment));
        } catch (ResourceNotFoundException e) {
            logger.error("User or appointment not found while updating appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User or appointment not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error updating appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error updating appointment: " + e.getMessage(), null));
        }
    }


    @PostMapping("/{appointmentId}/feedback")
    public ResponseEntity<ApiResponseDTO<Void>> submitFeedback(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long appointmentId, @RequestBody @Valid FeedbackDTO feedbackDTO) {
        try {
            String username = userDetails.getUsername();
            Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
            appointmentService.submitFeedback(userId, appointmentId, feedbackDTO);
            return ResponseEntity.ok(new ApiResponseDTO<>("Feedback submitted successfully", null));
        } catch (ResourceNotFoundException e) {
            logger.error("User or appointment not found while submitting feedback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User or appointment not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error submitting feedback: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error submitting feedback: " + e.getMessage(), null));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String username = userDetails.getUsername();
            Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentHistory(userId, page, size);
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointment history successfully", appointments));
        } catch (ResourceNotFoundException e) {
            logger.error("User not found while fetching appointment history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("User not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error fetching appointment history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error fetching appointment history: " + e.getMessage(), null));
        }
    }

    @GetMapping("/specialist")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsForSpecialist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String username = userDetails.getUsername();
            Long specialistId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + username)).getUserId();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsForSpecialist(specialistId, page, size);
            return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments for specialist successfully", appointments));
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found while fetching appointments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO<>("Specialist not found: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error fetching appointments for specialist: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>("Error fetching appointments: " + e.getMessage(), null));
        }
    }

}
