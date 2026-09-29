package com.rikkei.doctorservice.service;

import com.rikkei.doctorservice.dto.DoctorRequest;
import com.rikkei.doctorservice.dto.DoctorResponse;
import com.rikkei.doctorservice.entity.Doctor;
import com.rikkei.doctorservice.repository.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public DoctorResponse create(DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setName(request.name());
        doctor.setSpecialization(request.specialization());
        doctor.setExperienceYears(request.experienceYears());
        doctor.setEmail(request.email());
        doctor.setStatus(request.status());
        return toResponse(doctorRepository.save(doctor));
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponse findById(Long id) {
        return doctorRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy bác sĩ có ID: " + id));
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(doctor.getId(), doctor.getName(), doctor.getSpecialization());
    }
}

