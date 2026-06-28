package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.appointment.request.AppointmentRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentRescheduleRequestDto;
import com.spring.petcareConnect.dtos.appointment.request.AppointmentUpdateRequestDto;
import com.spring.petcareConnect.dtos.appointment.response.AppointmentListResponseDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

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
    public AppointmentResponseDto createAppointment(AppointmentRequestDto appointmentRequestDto) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new APIException("No logged-in user"));

        User user = getUserByEmailOrThrow(email);

        Pet pet = petRepository.findByPetIdAndPetOwner(appointmentRequestDto.getPetId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "Id", appointmentRequestDto.getPetId()));

        Specialist specialist = specialistRepository.findById(appointmentRequestDto.getSpecialistId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", "Id", appointmentRequestDto.getSpecialistId()));

        if (!specialist.isAvailable()) {
            throw new ConflictException("The selected specialist is not currently available.");
        }

        Integer slotDuration = specialist.getSlotDuration();
        LocalDateTime startTime = appointmentRequestDto.getAppointmentDate();
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
        appointment.setNotes(appointmentRequestDto.getNotes() != null ? appointmentRequestDto.getNotes().trim() : null);
        appointment.setAppointmentStatus(appointmentRequestDto.getAppointmentStatus());

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
    public AppointmentResponseDto updateAppointment(Long appointmentId, AppointmentUpdateRequestDto appointmentUpdateRequestDto) {
        String email = AuthUtils.loggedInEmail() .orElseThrow(() -> new APIException("No logged-in user"));

        User user = getUserByEmailOrThrow(email);
        Appointment appointment = appointmentRepository.findByAppointmentIdAndPetOwner(appointmentId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "Id", appointmentId + " for current user"));

        if (!appointment.getPetOwner().getUserId().equals(user.getUserId())) {
            throw new APIException("You cannot update someone else's appointment.");
        }

        if (appointmentUpdateRequestDto.getNotes() != null) {
            appointment.setNotes(appointmentUpdateRequestDto.getNotes().trim());
        }

        Appointment saved = appointmentRepository.save(appointment);
        notificationService.sendAppointmentUpdated(saved);

        AppointmentResponseDto responseDto = modelMapper.map(saved, AppointmentResponseDto.class);
        responseDto.setPetName(appointment.getPet().getPetName());
        responseDto.setSpecialistFirstName(appointment.getSpecialist().getUser().getFirstName());
        responseDto.setSpecialistLastName(appointment.getSpecialist().getUser().getLastName());

        return responseDto;
    }

    @Override
    @Transactional
    public AppointmentResponseDto rescheduleAppointment(Long appointmentId, AppointmentRescheduleRequestDto appointmentRescheduleRequestDto) {
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> new APIException("No logged-in user"));

        User user = getUserByEmailOrThrow(email);
        Appointment appointment = appointmentRepository.findByAppointmentIdAndPetOwner(appointmentId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment", "Id", appointmentId + " for current user"));

        if (!appointment.getPetOwner().getUserId().equals(user.getUserId())) {
            throw new APIException("You cannot reschedule someone else's appointment.");
        }

        Specialist specialist = appointment.getSpecialist();
        LocalDateTime newStartTime = appointmentRescheduleRequestDto.getNewAppointmentDate();
        LocalDateTime newEndTime = newStartTime.plusMinutes(specialist.getSlotDuration());

        if (!specialist.isAvailable()) {
            throw new ConflictException("The selected specialist is not currently available.");
        }

        if (newStartTime.toLocalTime().isBefore(specialist.getWorkingHoursStart()) ||
                newEndTime.toLocalTime().isAfter(specialist.getWorkingHoursEnd())) {
            throw new ConflictException("Appointment time must be within specialist working hours.");
        }

        if (appointmentRepository.isSpecialistBusy(specialist.getSpecialistId(), newStartTime, newEndTime)) {
            throw new ConflictException("Specialist is already booked during this time.");
        }

        if (appointmentRepository.isPetBusy(appointment.getPet().getPetId(), newStartTime, newEndTime)) {
            throw new ConflictException(
                    appointment.getPet().getPetName() + " already has another appointment at this time.");
        }

        appointment.setAppointmentDate(newStartTime);
        appointment.setDuration(specialist.getSlotDuration());
        appointment.setAppointmentStatus(AppointmentStatus.RESCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);
        notificationService.sendAppointmentRescheduled(saved);

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

    @Override
    public AppointmentListResponseDto getAllAppointmentsForUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        logger.info("Fetching all appointments for user with pagination page={} size={} sortBy={} sortOrder={}",
                pageNumber, pageSize, sortBy, sortOrder);
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during appointment list retrieval");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Appointment> appointmentPage = appointmentRepository.findAllByPetOwner(user, pageable);
        logger.debug("Found {} appointments for user {}", appointmentPage.getTotalElements(), email);
        return buildResponse(appointmentPage);
    }

    @Override
    public AppointmentResponseDto getAppointmentOfUserById(Long appointmentId) {
        logger.info("Fetching appointment with id {}", appointmentId);
        String email = AuthUtils.loggedInEmail().orElseThrow(() -> {
            logger.error("No logged-in user found during appointment fetch");
            return new APIException("No logged-in user");
        });
        User user = getUserByEmailOrThrow(email);

        Appointment appointment = appointmentRepository.findByAppointmentIdAndPetOwner(appointmentId, user).orElseThrow(() -> {
            logger.error("Appointment not found with id {}", appointmentId);
            return new ResourceNotFoundException("Appointment", "id", appointmentId);
        });
        logger.info("Successfully fetched appointment '{}' with id {}", appointment.getPet().getPetName(), appointment.getAppointmentId());
        return convertToDto(appointment);
    }

    private AppointmentListResponseDto buildResponse(Page<Appointment> appointmentPage) {
        if (appointmentPage.isEmpty()) {
            throw new APIException("No appointment exists for this user");
        }

        List<AppointmentResponseDto> appointments = appointmentPage.getContent().stream().map(this::convertToDto).toList();
        return new AppointmentListResponseDto(appointments, appointmentPage.getNumber(), appointmentPage.getSize(), appointmentPage.getTotalElements(), appointmentPage.getTotalPages(), appointmentPage.isLast());
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));
    }

    private Pageable buildPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }

    private AppointmentResponseDto convertToDto(Appointment appointment) {
        AppointmentResponseDto dto = modelMapper.map(appointment, AppointmentResponseDto.class);

        if (appointment.getSpecialist() != null && appointment.getSpecialist().getUser() != null) {
            dto.setSpecialistFirstName(appointment.getSpecialist().getUser().getFirstName());
            dto.setSpecialistLastName(appointment.getSpecialist().getUser().getLastName());
        }

        if (appointment.getPetOwner() != null) {
            dto.setPetOwnerFirstName(appointment.getPetOwner().getFirstName());
            dto.setPetOwnerLastName(appointment.getPetOwner().getLastName());
        }

        return dto;
    }
}
