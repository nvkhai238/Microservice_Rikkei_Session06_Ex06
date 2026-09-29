package com.rikkei.patientservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank(message = "Họ tên bệnh nhân không được để trống") String fullName,
        @Past(message = "Ngày sinh phải ở trong quá khứ") LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String address,
        String medicalHistory
) {
}

