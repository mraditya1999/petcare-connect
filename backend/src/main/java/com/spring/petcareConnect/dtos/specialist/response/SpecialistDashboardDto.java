package com.spring.petcareConnect.dtos.specialist.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistDashboardDto {
    private long totalAppointments;
    private long upcomingAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private double averageRating;
}
