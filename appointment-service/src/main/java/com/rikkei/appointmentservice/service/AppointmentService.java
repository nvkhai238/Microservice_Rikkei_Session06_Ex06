package com.rikkei.appointmentservice.service;

import com.rikkei.appointmentservice.dto.AppointmentRequest;
import com.rikkei.appointmentservice.dto.AppointmentResponse;
import com.rikkei.appointmentservice.dto.DoctorSummary;
import com.rikkei.appointmentservice.dto.PatientSummary;
import com.rikkei.appointmentservice.entity.Appointment;
import com.rikkei.appointmentservice.error.DownstreamServiceUnavailableException;
import com.rikkei.appointmentservice.repository.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate;

    public AppointmentService(AppointmentRepository appointmentRepository, RestTemplate restTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        verifyPatient(request.patientId());
        verifyDoctor(request.doctorId());

        Appointment appointment = new Appointment();
        appointment.setPatientId(request.patientId());
        appointment.setDoctorId(request.doctorId());
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setReason(request.reason());
        appointment.setStatus("PENDING");

        return toResponse(appointmentRepository.save(appointment));
    }

    private void verifyPatient(Long patientId) {
        try {
            restTemplate.getForObject(
                    "http://PATIENT-SERVICE/api/v1/patients/{id}",
                    PatientSummary.class,
                    patientId
            );
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bệnh nhân không tồn tại: " + patientId, exception);
        } catch (RestClientException | IllegalStateException exception) {
            throw new DownstreamServiceUnavailableException(
                    "Patient-Service tạm thời không phản hồi. Vui lòng thử lại sau.", exception);
        }
    }

    private void verifyDoctor(Long doctorId) {
        try {
            restTemplate.getForObject(
                    "http://DOCTOR-SERVICE/api/v1/doctors/{id}",
                    DoctorSummary.class,
                    doctorId
            );
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bác sĩ không tồn tại: " + doctorId, exception);
        } catch (RestClientException | IllegalStateException exception) {
            throw new DownstreamServiceUnavailableException(
                    "Doctor-Service tạm thời không phản hồi. Vui lòng thử lại sau.", exception);
        }
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(), appointment.getPatientId(), appointment.getDoctorId(),
                appointment.getAppointmentDate(), appointment.getReason(), appointment.getStatus()
        );
    }
}
