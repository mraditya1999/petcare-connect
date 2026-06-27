package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.entities.Appointment;
import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.EmailType;
import com.spring.petcareConnect.repositories.jpa.AppointmentRepository;
import com.spring.petcareConnect.services.EmailService;
import com.spring.petcareConnect.services.NotificationService;
import com.spring.petcareConnect.services.SmsService;
import com.spring.petcareConnect.exceptions.SmsSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${twilio.defaultToNumber}")
    private String defaultNumber;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final DateTimeFormatter USER_FRIENDLY_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");


    private final SmsService smsService;
    private final EmailService emailService;
    private final AppointmentRepository appointmentRepository;

    public NotificationServiceImpl(SmsService smsService,
                                   AppointmentRepository appointmentRepository,
                                   EmailService emailService) {
        this.smsService = smsService;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    @Override
    public void sendAppointmentCreated(Appointment appointment) {
        logger.info("Sending appointment created notification for appointmentId={}", appointment.getAppointmentId());
        User owner = appointment.getPetOwner();

        emailService.sendEmail(owner, EmailType.APPOINTMENT_CREATED);
        String message = "Your appointment with " + appointment.getSpecialist().getUser().getFirstName() + " is scheduled for";
        safeSendSms(owner.getMobileNumber(), message, appointment.getAppointmentDate());
    }

    @Override
    public void sendAppointmentUpdated(Appointment appointment) {
        logger.info("Sending appointment updated notification for appointmentId={}", appointment.getAppointmentId());
        User owner = appointment.getPetOwner();

        emailService.sendEmail(owner, EmailType.APPOINTMENT_UPDATED);
        String message = "Your appointment with " + appointment.getSpecialist().getUser().getFirstName() + " has been rescheduled to";
        safeSendSms(owner.getMobileNumber(), message, appointment.getAppointmentDate());
    }

    @Override
    public void sendAppointmentCancelled(Appointment appointment) {
        logger.info("Sending appointment cancelled notification for appointmentId={}", appointment.getAppointmentId());
        User owner = appointment.getPetOwner();

        emailService.sendEmail(owner, EmailType.APPOINTMENT_CANCELLED);
        String message = "Your appointment with " + appointment.getSpecialist().getUser().getFirstName() + " scheduled for";
        safeSendSms(owner.getMobileNumber(), message + " (cancelled)", appointment.getAppointmentDate());
    }

    @Override
    public void sendReminder(Appointment appointment) {
        logger.info("Sending appointment reminder for appointmentId={}", appointment.getAppointmentId());
        User owner = appointment.getPetOwner();

        emailService.sendEmail(owner, EmailType.APPOINTMENT_REMINDER);
        String message = "Reminder: Appointment for " + appointment.getPet().getPetName() + " at";
        safeSendSms(owner.getMobileNumber(), message, appointment.getAppointmentDate());
    }

    @Scheduled(fixedRate = 3600000) // every hour
    public void sendUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindow = now.plusHours(24);

        logger.debug("Checking for upcoming appointments between {} and {}", now, reminderWindow);
        List<Appointment> upcoming = appointmentRepository.findAppointmentsBetween(now, reminderWindow);
        upcoming.forEach(this::sendReminder);
    }

    private void safeSendSms(String to, String message, LocalDateTime appointmentDate) {
        String recipient = (to == null || to.isBlank()) ? defaultNumber : to;
        try {
            String formattedDate = appointmentDate.format(USER_FRIENDLY_FORMAT);
            String finalMessage = message + " (" + formattedDate + ")";

            smsService.sendSms(recipient, finalMessage);
            logger.info("SMS sent to {} with message '{}'", recipient, finalMessage);
        } catch (SmsSendException e) {
            logger.error("Failed to send SMS to {}: {}", recipient, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending SMS to {}: {}", recipient, e.getMessage(), e);
        }
    }
}
