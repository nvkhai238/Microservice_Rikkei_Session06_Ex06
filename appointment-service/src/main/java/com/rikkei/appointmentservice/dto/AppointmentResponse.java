package com.rikkei.appointmentservice.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        LocalDateTime appointmentDate,
        String reason,
        String status
) {
}

