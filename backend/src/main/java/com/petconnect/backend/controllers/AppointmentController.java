package com.petconnect.backend.controllers;

import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/appointments")
public class AppointmentController
{
    @Autowired
    private AppointmentService appointmentService;


    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentService.createAppointment(
                appointment.getPetOwner().getUserId(),
                appointment.getPet().getPetId(),
                appointment.getSpecialist().getSpecialistId(),
                appointment.getDate()
        );
        return ResponseEntity.ok(savedAppointment);
    }



}
