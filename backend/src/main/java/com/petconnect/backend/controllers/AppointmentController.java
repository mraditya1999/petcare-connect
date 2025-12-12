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
            @RequestParam(defaultValue = "10") int size) throws ResourceNotFoundException {
        String username = userDetails.getUsername();
        Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
        Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPetOwner(userId, page, size);
        return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments for pet owner successfully", appointments));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException {
        Long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found")).getUserId();
        Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPet(petId, page, size, userId);
        return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments for pet successfully", appointments));
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByStatus(
            @PathVariable Appointment.AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException {
        Long userId = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found")).getUserId();
        Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStatusForUser(status, page, size, userId);
        return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments by status successfully", appointments));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> createAppointment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO) throws ResourceNotFoundException {
        String username = userDetails.getUsername();
        Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(userId, appointmentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>("Appointment created successfully", createdAppointment));
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updateAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId,
            @RequestBody @Valid AppointmentUpdateRequestDTO updatedAppointmentDTO) throws ResourceNotFoundException {
        String username = userDetails.getUsername();
        Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(appointmentId, userId, updatedAppointmentDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>("Appointment updated successfully", updatedAppointment));
    }


    @PostMapping("/{appointmentId}/feedback")
    public ResponseEntity<ApiResponseDTO<Void>> submitFeedback(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long appointmentId, @RequestBody @Valid FeedbackDTO feedbackDTO) throws ResourceNotFoundException {
        String username = userDetails.getUsername();
        Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
        appointmentService.submitFeedback(userId, appointmentId, feedbackDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>("Feedback submitted successfully", null));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ResourceNotFoundException {
        String username = userDetails.getUsername();
        Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
        Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentHistory(userId, page, size);
        return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointment history successfully", appointments));
    }

    @GetMapping("/specialist")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsForSpecialist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ResourceNotFoundException {
        String username = userDetails.getUsername();
        Long specialistId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Specialist not found with email: " + username)).getUserId();
        Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsForSpecialist(specialistId, page, size);
        return ResponseEntity.ok(new ApiResponseDTO<>("Fetched appointments for specialist successfully", appointments));
    }

}
