package com.rikkei.appointmentservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull @Positive Long patientId,
        @NotNull @Positive Long doctorId,
        @NotNull @Future(message = "Ngày khám phải ở trong tương lai") LocalDateTime appointmentDate,
        @NotBlank(message = "Lý do khám không được để trống") String reason
) {
}

