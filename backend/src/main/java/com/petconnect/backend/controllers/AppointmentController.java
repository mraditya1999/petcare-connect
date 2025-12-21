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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Operation(
            summary = "Get appointments by pet owner",
            description = "Fetches paginated appointments for the authenticated pet owner",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Appointments fetched successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/petOwner")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByPetOwner(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Min(1) int size) {
        String username = userDetails.getUsername();
        Long userId = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username)).getUserId();
        Page<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPetOwner(userId, page, size);
        return ResponseEntityUtil.page(appointments, "Fetched appointments for pet owner successfully");
    }

    @Operation(
            summary = "Get appointments by pet ID",
            description = "Fetches paginated appointments for a specific pet, only accessible to authorized users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Appointments fetched successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    @GetMapping("/pet/{petId}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByPet(
            @Parameter(description = "Pet ID") @PathVariable Long petId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails) {
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


    @Operation(
            summary = "Get appointments by status",
            description = "Fetches paginated appointments for the authenticated user filtered by appointment status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Appointments fetched successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsByStatus(
            @Parameter(description = "Appointment status") @PathVariable Appointment.AppointmentStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails) {
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

    @Operation(
            summary = "Create a new appointment",
            description = "Creates a new appointment for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Appointment created successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> createAppointment(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Appointment creation data") @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO) {
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

    @Operation(
            summary = "Update an appointment",
            description = "Updates an appointment by ID for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Appointment updated successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponseDTO<AppointmentResponseDTO>> updateAppointment(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Parameter(description = "Updated appointment data") @RequestBody @Valid AppointmentUpdateRequestDTO updatedAppointmentDTO) {
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


    @Operation(
            summary = "Submit feedback for appointment",
            description = "Submits feedback for a specific appointment by the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Feedback submitted successfully"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    @PostMapping("/{appointmentId}/feedback")
    public ResponseEntity<ApiResponseDTO<Void>> submitFeedback(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Appointment ID") @PathVariable Long appointmentId,
            @Parameter(description = "Feedback data") @RequestBody @Valid FeedbackDTO feedbackDTO) {
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

    @Operation(
            summary = "Get appointment history",
            description = "Fetches paginated appointment history for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Appointment history fetched successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/history")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentHistory(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
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

    @Operation(
            summary = "Get appointments for specialist",
            description = "Fetches paginated appointments for the authenticated specialist",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Appointments fetched successfully",
                            content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Specialist not found")
            }
    )
    @GetMapping("/specialist")
    public ResponseEntity<ApiResponseDTO<Page<AppointmentResponseDTO>>> getAppointmentsForSpecialist(
            @Parameter(description = "Authenticated user details") @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
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
