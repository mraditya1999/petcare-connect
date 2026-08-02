package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.dtos.specialist.request.AdminSpecialistUpdateRequest;
import com.spring.petcareConnect.dtos.specialist.request.SpecialistProfileUpdateRequestDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistDashboardDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistListResponseDto;
import com.spring.petcareConnect.dtos.specialist.response.SpecialistResponseDto;
import com.spring.petcareConnect.entities.Appointment;
import com.spring.petcareConnect.entities.Specialist;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.AppointmentStatus;
import com.spring.petcareConnect.enums.RoleName;
import com.spring.petcareConnect.exceptions.APIException;
import com.spring.petcareConnect.exceptions.ResourceNotFoundException;
import com.spring.petcareConnect.repositories.jpa.AppointmentRepository;
import com.spring.petcareConnect.repositories.jpa.SpecialistRepository;
import com.spring.petcareConnect.repositories.jpa.UserRepository;
import com.spring.petcareConnect.services.ServiceMappingSupport;
import com.spring.petcareConnect.services.SpecialistService;
import com.spring.petcareConnect.utils.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialistServiceImpl implements SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceMappingSupport mappingSupport;

    public SpecialistServiceImpl(SpecialistRepository specialistRepository,
                                 AppointmentRepository appointmentRepository,
                                 UserRepository userRepository,
                                 ServiceMappingSupport mappingSupport) {
        this.specialistRepository = specialistRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.mappingSupport = mappingSupport;
    }

    @Override
    public SpecialistListResponseDto getSpecialists(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = mappingSupport.buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Specialist> specialistPage = specialistRepository.findAll(pageable);
        return buildResponse(specialistPage);
    }

    @Override
    public SpecialistResponseDto getSpecialistById(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", "Id", specialistId));
        return mapToDto(specialist);
    }

    @Override
    public SpecialistDashboardDto getSpecialistDashboard() {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new APIException("No logged-in user"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));

        boolean isSpecialist = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ROLE_SPECIALIST);
        if (!isSpecialist) {
            throw new APIException("Only specialists can access this dashboard");
        }

        Specialist specialist = specialistRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", "userId", user.getUserId()));

        List<Appointment> appointments = appointmentRepository.findBySpecialistSpecialistId(specialist.getSpecialistId());
        long totalAppointments = appointments.size();
        long upcomingAppointments = appointments.stream()
                .filter(a -> a.getAppointmentStatus() == AppointmentStatus.SCHEDULED)
                .count();
        long completedAppointments = appointments.stream()
                .filter(a -> a.getAppointmentStatus() == AppointmentStatus.COMPLETED)
                .count();
        long cancelledAppointments = appointments.stream()
                .filter(a -> a.getAppointmentStatus() == AppointmentStatus.CANCELLED)
                .count();
        double averageRating = appointments.stream()
                .filter(a -> a.getRating() != null)
                .mapToDouble(Appointment::getRating)
                .average()
                .orElse(0.0);

        return new SpecialistDashboardDto(
                totalAppointments,
                upcomingAppointments,
                completedAppointments,
                cancelledAppointments,
                averageRating
        );
    }

    @Override
    public SpecialistResponseDto updateSpecialistByAdmin(Long specialistId, AdminSpecialistUpdateRequest request) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", "Id", specialistId));

        specialist.setAbout(request.getAbout());
        specialist.setAvailable(request.getAvailable());
        specialist.setSlotDuration(request.getSlotDuration());
        specialist.setSpecialization(request.getSpecialization());
        specialist.setExperienceYears(request.getExperienceYears());
        specialist.setConsultationFee(request.getConsultationFee());
        specialist.setWorkingHoursStart(request.getWorkingHoursStart());
        specialist.setWorkingHoursEnd(request.getWorkingHoursEnd());
        specialist.setDaysAvailable(request.getDaysAvailable());
        specialist.setLocation(request.getLocation());

        specialistRepository.save(specialist);
        return mapToDto(specialist);
    }

    @Override
    public SpecialistResponseDto updateCurrentSpecialist(SpecialistProfileUpdateRequestDto request) {
        String email = AuthUtils.loggedInEmail()
                .orElseThrow(() -> new APIException("No logged-in user"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.byField("User", "email", email));

        boolean isSpecialist = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ROLE_SPECIALIST);
        if (!isSpecialist) {
            throw new APIException("Only specialists can update their profile");
        }

        Specialist specialist = specialistRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist", "userId", user.getUserId()));

        if (request.getAbout() != null) specialist.setAbout(request.getAbout());
        if (request.getAvailable() != null) specialist.setAvailable(request.getAvailable());
        if (request.getSlotDuration() != null) specialist.setSlotDuration(request.getSlotDuration());
        if (request.getSpecialization() != null) specialist.setSpecialization(request.getSpecialization());
        if (request.getExperienceYears() != null) specialist.setExperienceYears(request.getExperienceYears());
        if (request.getConsultationFee() != null) specialist.setConsultationFee(request.getConsultationFee());
        if (request.getWorkingHoursStart() != null) specialist.setWorkingHoursStart(request.getWorkingHoursStart());
        if (request.getWorkingHoursEnd() != null) specialist.setWorkingHoursEnd(request.getWorkingHoursEnd());
        if (request.getDaysAvailable() != null) specialist.setDaysAvailable(request.getDaysAvailable());
        if (request.getLocation() != null) specialist.setLocation(request.getLocation());

        specialistRepository.save(specialist);
        return mapToDto(specialist);
    }

    private SpecialistListResponseDto buildResponse(Page<Specialist> specialistPage) {
        List<SpecialistResponseDto> content = specialistPage.getContent().stream()
                .map(this::mapToDto)
                .toList();

        return new SpecialistListResponseDto(
                content,
                specialistPage.getNumber(),
                specialistPage.getSize(),
                specialistPage.getTotalElements(),
                specialistPage.getTotalPages(),
                specialistPage.isLast()
        );
    }

    private SpecialistResponseDto mapToDto(Specialist specialist) {
        SpecialistResponseDto dto = new SpecialistResponseDto();
        dto.setSpecialistId(specialist.getSpecialistId());
        dto.setUserId(specialist.getUser() != null ? specialist.getUser().getUserId() : null);
        dto.setFirstName(specialist.getUser() != null ? specialist.getUser().getFirstName() : null);
        dto.setLastName(specialist.getUser() != null ? specialist.getUser().getLastName() : null);
        dto.setAbout(specialist.getAbout());
        dto.setAvailable(specialist.isAvailable());
        dto.setSlotDuration(specialist.getSlotDuration());
        dto.setSpecialization(specialist.getSpecialization());
        dto.setExperienceYears(specialist.getExperienceYears());
        dto.setRating(specialist.getRating());
        dto.setConsultationFee(specialist.getConsultationFee());
        dto.setWorkingHoursStart(specialist.getWorkingHoursStart());
        dto.setWorkingHoursEnd(specialist.getWorkingHoursEnd());
        dto.setDaysAvailable(specialist.getDaysAvailable());
        dto.setLocation(specialist.getLocation());
        dto.setVerified(specialist.getUser() != null && specialist.getUser().isVerified());
        return dto;
    }
}
