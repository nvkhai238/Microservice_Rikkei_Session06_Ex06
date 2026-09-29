package com.rikkei.appointmentservice.service;

import com.rikkei.appointmentservice.dto.AppointmentRequest;
import com.rikkei.appointmentservice.dto.PatientSummary;
import com.rikkei.appointmentservice.error.DownstreamServiceUnavailableException;
import com.rikkei.appointmentservice.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void doesNotSaveWhenDoctorServiceIsDown() {
        when(restTemplate.getForObject(
                eq("http://PATIENT-SERVICE/api/v1/patients/{id}"),
                eq(PatientSummary.class), eq(1L)))
                .thenReturn(new PatientSummary(1L, "Nguyễn Văn An"));
        when(restTemplate.getForObject(
                eq("http://DOCTOR-SERVICE/api/v1/doctors/{id}"),
                eq(com.rikkei.appointmentservice.dto.DoctorSummary.class), eq(2L)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        AppointmentRequest request = new AppointmentRequest(
                1L, 2L, LocalDateTime.now().plusDays(1), "Khám tổng quát");

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(DownstreamServiceUnavailableException.class)
                .hasMessage("Doctor-Service tạm thời không phản hồi. Vui lòng thử lại sau.");
        verify(appointmentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}

