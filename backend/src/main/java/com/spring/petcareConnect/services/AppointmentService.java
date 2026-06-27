package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.appointment.request.AppointmentRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentUpdateRequestDto;
import com.spring.petcareConnect.dtos.appointment.response.AppointmentResponseDto;

public interface AppointmentService {
    AppointmentResponseDto createAppointment(AppointmentRequestDto appointmentRequestDTO);

    AppointmentResponseDto updateAppointment(Long appointmentId, AppointmentUpdateRequestDto dto);

    AppointmentResponseDto cancelAppointment(Long appointmentId);
}
