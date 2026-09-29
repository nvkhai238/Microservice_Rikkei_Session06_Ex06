package com.rikkei.doctorservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoctorRequest(
        @NotBlank(message = "Tên bác sĩ không được để trống") String name,
        @NotBlank(message = "Chuyên khoa không được để trống") String specialization,
        @Min(value = 0, message = "Số năm kinh nghiệm không hợp lệ") Integer experienceYears,
        @Email(message = "Email không đúng định dạng") String email,
        @NotNull(message = "Trạng thái không được để trống") Boolean status
) {
}

