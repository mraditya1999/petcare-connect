package com.petconnect.backend.controllers;

import com.petconnect.backend.dto.AppointmentDTO;
import com.petconnect.backend.entity.Appointment;
import com.petconnect.backend.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController
{
    @Autowired
    private AppointmentService appointmentService;


    @PostMapping("/create")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO savedAppointment = appointmentService.createAppointment(appointmentDTO);
        return ResponseEntity.ok(savedAppointment);
    }


@GetMapping("/all")
public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
    List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
    return ResponseEntity.ok(appointments);
}

    @GetMapping("/get/{id}")  // ✅ This should match the correct endpoint
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDTO appointmentDTO) {
        System.out.println("Update Request Received for ID: " + id);
        System.out.println("Incoming JSON: " + appointmentDTO);
        AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentDTO);
        System.out.println("Updated Appointment: " + updatedAppointment);
        return ResponseEntity.ok(updatedAppointment);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok("Appointment deleted successfully");
    }




}
