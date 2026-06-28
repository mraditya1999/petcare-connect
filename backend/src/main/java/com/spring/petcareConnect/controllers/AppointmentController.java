package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.config.AppConstants;
import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentRescheduleRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentUpdateRequestDto;
import com.spring.petcareConnect.dtos.appointment.response.AppointmentListResponseDto;
import com.spring.petcareConnect.dtos.appointment.response.AppointmentResponseDto;
import com.spring.petcareConnect.services.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<AppointmentResponseDto>> createAppointment(@RequestBody @Valid AppointmentRequestDto appointmentRequestDTO) {
        AppointmentResponseDto appointmentResponseDto = appointmentService.createAppointment(appointmentRequestDTO);
        CustomApiResponse<AppointmentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENT_CREATED, appointmentResponseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<CustomApiResponse<AppointmentResponseDto>> updateAppointment(@PathVariable Long appointmentId, @RequestBody @Valid AppointmentUpdateRequestDto appointmentUpdateRequestDto) {
        AppointmentResponseDto appointmentResponseDto = appointmentService.updateAppointment(appointmentId, appointmentUpdateRequestDto);
        CustomApiResponse<AppointmentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENT_UPDATED, appointmentResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<CustomApiResponse<AppointmentResponseDto>> cancelAppointment(@PathVariable Long appointmentId) {
        AppointmentResponseDto appointmentResponseDto = appointmentService.cancelAppointment(appointmentId);
        CustomApiResponse<AppointmentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENT_CANCELLED, appointmentResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<AppointmentListResponseDto>> getAllAppointmentsForUser(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                                                   @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                                                   @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_APPOINTMENTS_BY, required = false) String sortBy,
                                                                                                   @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder) {
        AppointmentListResponseDto appointmentListResponseDto = appointmentService.getAllAppointmentsForUser(pageNumber, pageSize, sortBy, sortOrder);
        CustomApiResponse<AppointmentListResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENTS_FETCHED, appointmentListResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<CustomApiResponse<AppointmentResponseDto>> getAppointmentOfUserById(@PathVariable Long appointmentId) {
        AppointmentResponseDto appointmentResponseDto = appointmentService.getAppointmentOfUserById(appointmentId);
        CustomApiResponse<AppointmentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENT_FETCHED, appointmentResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{appointmentId}/reschedule")
    public ResponseEntity<CustomApiResponse<AppointmentResponseDto>> rescheduleAppointment(@PathVariable Long appointmentId, @RequestBody @Valid AppointmentRescheduleRequestDto appointmentRescheduleRequestDto) {
        AppointmentResponseDto responseDto = appointmentService.rescheduleAppointment(appointmentId, appointmentRescheduleRequestDto);
        CustomApiResponse<AppointmentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENT_RESCHEDULED, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
