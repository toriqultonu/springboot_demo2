package com.tts.patientservice.service;

import com.tts.patientservice.dto.PatientRequestDTO;
import com.tts.patientservice.dto.PatientResponseDTO;
import com.tts.patientservice.exception.EmailAlreadyExistsException;
import com.tts.patientservice.exception.PatientNotFoundException;
import com.tts.patientservice.mapper.PatientMapper;
import com.tts.patientservice.model.Patient;
import com.tts.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDTO.getEmail() + " already exists");
        }

        Patient patient = patientRepository.save(PatientMapper.toPatient(patientRequestDTO));
        return PatientMapper.toPatientResponseDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){

        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with Id: " + id));
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDTO.getEmail() + " already exists");
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toPatientResponseDTO(updatedPatient);
    }
}
