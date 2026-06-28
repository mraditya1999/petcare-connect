package com.spring.petcareConnect.services;

import com.spring.petcareConnect.dtos.appointment.request.AppointmentRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentRescheduleRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentUpdateRequestDto;
import com.spring.petcareConnect.dtos.appointment.response.AppointmentListResponseDto;
import com.spring.petcareConnect.dtos.appointment.response.AppointmentResponseDto;

public interface AppointmentService {
    AppointmentResponseDto createAppointment(AppointmentRequestDto appointmentRequestDTO);

    AppointmentResponseDto updateAppointment(Long appointmentId, AppointmentUpdateRequestDto appointmentUpdateRequestDto);

    AppointmentResponseDto cancelAppointment(Long appointmentId);

    AppointmentListResponseDto getAllAppointmentsForUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AppointmentResponseDto getAppointmentOfUserById(Long appointmentId);

    AppointmentResponseDto rescheduleAppointment(Long appointmentId, AppointmentRescheduleRequestDto appointmentRescheduleRequestDto);
}
