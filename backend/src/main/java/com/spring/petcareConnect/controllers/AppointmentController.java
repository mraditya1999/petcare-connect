package com.spring.petcareConnect.controllers;

import com.spring.petcareConnect.config.ResponseMessages;
import com.spring.petcareConnect.dtos.CustomApiResponse;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentUpdateRequestDto;
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
        AppointmentResponseDto appointmentResponseDto = appointmentService.updateAppointment(appointmentId,appointmentUpdateRequestDto);
        CustomApiResponse<AppointmentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENT_UPDATED, appointmentResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<CustomApiResponse<AppointmentResponseDto>> cancelAppointment(@PathVariable Long appointmentId) {
        AppointmentResponseDto appointmentResponseDto = appointmentService.cancelAppointment(appointmentId);
        CustomApiResponse<AppointmentResponseDto> response = new CustomApiResponse<>(true, ResponseMessages.APPOINTMENT_CANCELLED, appointmentResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
