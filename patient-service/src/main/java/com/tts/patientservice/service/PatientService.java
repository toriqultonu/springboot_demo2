package com.tts.patientservice.service;

import com.tts.patientservice.dto.PatientResponseDTO;
import com.tts.patientservice.mapper.PatientMapper;
import com.tts.patientservice.model.Patient;
import com.tts.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO>getPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toPatientResponseDTO)
                .toList();
    }

}
