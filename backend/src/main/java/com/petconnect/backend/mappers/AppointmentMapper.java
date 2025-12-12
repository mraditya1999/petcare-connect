package com.petconnect.backend.mappers;

import com.petconnect.backend.dto.appointment.AppointmentRequestDTO;
import com.petconnect.backend.dto.appointment.AppointmentResponseDTO;
import com.petconnect.backend.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    @Mapping(source = "petOwnerId", target = "petOwner.userId")
    @Mapping(source = "petId", target = "pet.petId")
    @Mapping(source = "specialistId", target = "specialist.userId")
    Appointment toAppointmentEntity(AppointmentRequestDTO appointmentRequestDTO);

    @Mapping(source = "petOwner.userId", target = "petOwnerId")
    @Mapping(source = "pet.petId", target = "petId")
    @Mapping(source = "specialist.userId", target = "specialistId")
    @Mapping(source = "petOwner.firstName", target = "petOwnerName")
    @Mapping(source = "specialist.firstName", target = "specialistName")
    @Mapping(source = "pet.petName", target = "petName")
    @Mapping(target = "appointmentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    @Mapping(target = "rating", ignore = true)
    AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment);
}
