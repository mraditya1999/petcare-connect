package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.appointment.request.AppointmentRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentUpdateRequestDto;
import com.spring.petcareConnect.dtos.appointment.response.AppointmentResponseDto;
import com.spring.petcareConnect.entities.Appointment;
import com.spring.petcareConnect.entities.Pet;
import com.spring.petcareConnect.entities.Specialist;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.AppointmentStatus;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.exceptions.ConflictException;
import com.spring.petcareConnect.exceptions.ResourceNotFoundException;
import com.spring.petcareConnect.repositories.jpa.AppointmentRepository;
import com.spring.petcareConnect.repositories.jpa.PetRepository;
import com.spring.petcareConnect.repositories.jpa.SpecialistRepository;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.services.AppointmentService;
import com.spring.petcareConnect.services.NotificationService;
import com.spring.petcareConnect.utils.AuthUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final PetRepository petRepository;
    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    public AppointmentServiceImpl(PetRepository petRepository,
                                  SpecialistRepository specialistRepository,
                                  UserRepository userRepository,
                                  AppointmentRepository appointmentRepository, NotificationService notificationService,
                                  ModelMapper modelMapper) {
        this.petRepository = petRepository;
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new APIException("No logged-in user"));

        User user = getUserByEmailOrThrow(email);

        Pet pet = petRepository.findByPetIdAndPetOwner(dto.getPetId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "Id", dto.getPetId()));

        Specialist specialist = specialistRepository.findById(dto.getSpecialistId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", "Id", dto.getSpecialistId()));

        if (!specialist.isAvailable()) {
            throw new ConflictException("The selected specialist is not currently available.");
        }

        Integer slotDuration = specialist.getSlotDuration();
        LocalDateTime startTime = dto.getAppointmentDate();
        LocalDateTime endTime = startTime.plusMinutes(slotDuration);

        if (startTime.toLocalTime().isBefore(specialist.getWorkingHoursStart()) ||
                endTime.toLocalTime().isAfter(specialist.getWorkingHoursEnd())) {
            throw new ConflictException("Appointment time must be within specialist working hours.");
        }

        if (appointmentRepository.isSpecialistBusy(specialist.getSpecialistId(), startTime, endTime)) {
            throw new ConflictException("Specialist is already booked during this time.");
        }

        if (appointmentRepository.isPetBusy(pet.getPetId(), startTime, endTime)) {
            throw new ConflictException(pet.getPetName() + " already has another appointment at this time.");
        }

        Appointment appointment = new Appointment();
        appointment.setPetOwner(user);
        appointment.setPet(pet);
        appointment.setSpecialist(specialist);
        appointment.setAppointmentDate(startTime);
        appointment.setDuration(slotDuration);
        appointment.setNotes(dto.getNotes() != null ? dto.getNotes().trim() : null);
        appointment.setAppointmentStatus(dto.getAppointmentStatus());

        Appointment saved = appointmentRepository.save(appointment);

        notificationService.sendAppointmentCreated(saved);

        AppointmentResponseDto responseDto = modelMapper.map(saved, AppointmentResponseDto.class);
        responseDto.setPetName(pet.getPetName());
        responseDto.setSpecialistFirstName(specialist.getUser().getFirstName());
        responseDto.setSpecialistLastName(specialist.getUser().getLastName());

        return responseDto;
    }

    @Override
    @Transactional
    public AppointmentResponseDto updateAppointment(Long appointmentId, AppointmentUpdateRequestDto dto) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new APIException("No logged-in user"));

        User user = getUserByEmailOrThrow(email);

        Appointment appointment = appointmentRepository.findByAppointmentIdAndPetOwner(appointmentId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment", "Id", appointmentId + " for current user"));


        // Ensure the logged-in user owns this appointment
        if (!appointment.getPetOwner().getUserId().equals(user.getUserId())) {
            throw new APIException("You cannot update someone else's appointment.");
        }

        Specialist specialist = appointment.getSpecialist();

        // If date is being updated, validate again
        if (dto.getAppointmentDate() != null) {
            LocalDateTime startTime = dto.getAppointmentDate();
            LocalDateTime endTime = startTime.plusMinutes(specialist.getSlotDuration());

            if (startTime.toLocalTime().isBefore(specialist.getWorkingHoursStart()) ||
                    endTime.toLocalTime().isAfter(specialist.getWorkingHoursEnd())) {
                throw new ConflictException("Appointment time must be within specialist working hours.");
            }

            if (appointmentRepository.isSpecialistBusy(specialist.getSpecialistId(), startTime, endTime)) {
                throw new ConflictException("Specialist is already booked during this time.");
            }

            if (appointmentRepository.isPetBusy(appointment.getPet().getPetId(), startTime, endTime)) {
                throw new ConflictException(appointment.getPet().getPetName() + " already has another appointment at this time.");
            }

            appointment.setAppointmentDate(startTime);
            appointment.setDuration(specialist.getSlotDuration());
        }

        // Update notes if provided
        if (dto.getNotes() != null) {
            appointment.setNotes(dto.getNotes().trim());
        }

        // Update status if provided
        if (dto.getAppointmentStatus() != null) {
            appointment.setAppointmentStatus(dto.getAppointmentStatus());
        }

        Appointment saved = appointmentRepository.save(appointment);

        notificationService.sendAppointmentUpdated(saved);

        AppointmentResponseDto responseDto = modelMapper.map(saved, AppointmentResponseDto.class);
        responseDto.setPetName(appointment.getPet().getPetName());
        responseDto.setSpecialistFirstName(specialist.getUser().getFirstName());
        responseDto.setSpecialistLastName(specialist.getUser().getLastName());

        return responseDto;
    }

    @Override
    @Transactional
    public AppointmentResponseDto cancelAppointment(Long appointmentId) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new APIException("No logged-in user"));

        User user = getUserByEmailOrThrow(email);

        Appointment appointment = appointmentRepository.findByAppointmentIdAndPetOwner(appointmentId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment", "Id", appointmentId + " for current user"));

        Specialist specialist = appointment.getSpecialist();
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);

        notificationService.sendAppointmentCancelled(saved);

        AppointmentResponseDto responseDto = modelMapper.map(saved, AppointmentResponseDto.class);
        responseDto.setPetName(appointment.getPet().getPetName());
        responseDto.setSpecialistFirstName(specialist.getUser().getFirstName());
        responseDto.setSpecialistLastName(specialist.getUser().getLastName());

        return responseDto;
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));
    }
}
