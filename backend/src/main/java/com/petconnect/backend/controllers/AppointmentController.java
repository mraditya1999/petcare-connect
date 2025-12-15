package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.ApiResponseDTO;
import com.petconnect.backend.dto.appointment.AppointmentUpdateRequestDTO;
import com.petconnect.backend.dto.appointment.FeedbackDTO;
import com.petconnect.backend.dto.appointment.AppointmentRequestDTO;
import com.petconnect.backend.dto.appointment.AppointmentResponseDTO;
import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.exceptions.UnauthorizedAccessException;
import com.petconnect.backend.repositories.UserRepository;
import com.petconnect.backend.services.AppointmentService;
import com.petconnect.backend.utils.ResponseEntityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        String username = userDetails.getUsername();
        Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
        Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPetOwner(userId, page, size);
        return ResponseEntityUtil.page(appointments, "Fetched appointments for pet owner successfully");
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPet(petId, page, size, username);
            logger.info("Fetched appointments for pet ID: {} by user: {}", petId, username);
            return ResponseEntityUtil.page(appointments, "Fetched appointments for pet successfully");
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found", e);
            return ResponseEntityUtil.notFound(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access", e);
            return ResponseEntityUtil.forbidden(e.getMessage());
        }
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByStatus(
            @PathVariable Appointment.AppointmentStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStatusForUser(status, page, size, username);
            logger.info("Fetched appointments by status: {} for user: {}", status, username);
            return ResponseEntityUtil.page(appointments, "Fetched appointments by status successfully");
        } catch (ResourceNotFoundException e) {
            logger.error("User not found", e);
            return ResponseEntityUtil.notFound("User not found");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> createAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO) {
        try {
            String username = userDetails.getUsername();
            AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(username, appointmentRequestDTO);
            logger.info("Created appointment for user: {}", username);
            return ResponseEntityUtil.created("Appointment created successfully", createdAppointment);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntityUtil.notFound(e.getMessage());
        }
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updateAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId,
            @RequestBody @Valid AppointmentUpdateRequestDTO updatedAppointmentDTO) {
        try {
            String username = userDetails.getUsername();
            AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(appointmentId, username, updatedAppointmentDTO);
            logger.info("Updated appointment ID: {} for user: {}", appointmentId, username);
            return ResponseEntityUtil.ok("Appointment updated successfully", updatedAppointment);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found", e);
            return ResponseEntityUtil.notFound(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access", e);
            return ResponseEntityUtil.forbidden(e.getMessage());
        }
    }


    @PostMapping("/{appointmentId}/feedback")
    public ResponseEntity<ApiResponseDTO<Void>> submitFeedback(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId,
            @RequestBody @Valid FeedbackDTO feedbackDTO) {
        try {
            String username = userDetails.getUsername();
            appointmentService.submitFeedback(username, appointmentId, feedbackDTO);
            logger.info("Submitted feedback for appointment ID: {} by user: {}", appointmentId, username);
            return ResponseEntityUtil.ok("Feedback submitted successfully", null);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found", e);
            return ResponseEntityUtil.notFound(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            logger.error("Unauthorized access", e);
            return ResponseEntityUtil.forbidden(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String username = userDetails.getUsername();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentHistory(username, page, size);
            logger.info("Fetched appointment history for user: {}", username);
            return ResponseEntityUtil.page(appointments, "Fetched appointment history successfully");
        } catch (ResourceNotFoundException e) {
            logger.error("User not found", e);
            return ResponseEntityUtil.notFound("User not found");
        }
    }

    @GetMapping("/specialist")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsForSpecialist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String username = userDetails.getUsername();
            Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsForSpecialist(username, page, size);
            logger.info("Fetched appointments for specialist: {}", username);
            return ResponseEntityUtil.page(appointments, "Fetched appointments for specialist successfully");
        } catch (ResourceNotFoundException e) {
            logger.error("Specialist not found: {}", e.getMessage());
            return ResponseEntityUtil.notFound("Specialist not found");
        }
    }

}
