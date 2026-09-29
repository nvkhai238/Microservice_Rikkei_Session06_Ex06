package com.rikkei.patientservice.service;

import com.rikkei.patientservice.dto.PatientRequest;
import com.rikkei.patientservice.dto.PatientResponse;
import com.rikkei.patientservice.entity.Patient;
import com.rikkei.patientservice.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public PatientResponse create(PatientRequest request) {
        Patient patient = new Patient();
        patient.setFullName(request.fullName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhoneNumber(request.phoneNumber());
        patient.setAddress(request.address());
        patient.setMedicalHistory(request.medicalHistory());
        return toResponse(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy bệnh nhân có ID: " + id));
        return toResponse(patient);
    }

    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(), patient.getFullName(), patient.getDateOfBirth(),
                patient.getGender(), patient.getPhoneNumber(), patient.getAddress(),
                patient.getMedicalHistory()
        );
    }
}

