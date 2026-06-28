package com.spring.petcareConnect.services;

import com.spring.petcareConnect.entities.Appointment;

public interface NotificationService {
    void sendAppointmentCreated(Appointment appointment);
    void sendAppointmentUpdated(Appointment appointment);
    void sendAppointmentCancelled(Appointment appointment);
    void sendReminder(Appointment appointment);
    void sendAppointmentRescheduled(Appointment appointment);
}
