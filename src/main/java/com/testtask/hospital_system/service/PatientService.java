package com.testtask.hospital_system.service;

import com.testtask.hospital_system.model.Gender;
import com.testtask.hospital_system.model.Patient;
import com.testtask.hospital_system.repository.PatientRepository;
import io.grpc.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient create(String firstName, String lastName, String dateOfBirth, Gender sex) {
        Patient patient = new Patient(firstName, lastName, parseDate(dateOfBirth), sex);
        return patientRepository.save(patient);
    }

    public Patient update(Long id, String firstName, String lastName, String dateOfBirth, Gender sex) {
        Patient patient = findByIdOrThrow(id);

        if (!firstName.isBlank())   patient.setFirstName(firstName);
        if (!lastName.isBlank())    patient.setLastName(lastName);
        if (!dateOfBirth.isBlank()) patient.setDateOfBirth(parseDate(dateOfBirth));
        if (sex != Gender.UNKNOWN)  patient.setSex(sex);

        return patientRepository.save(patient);
    }

    public void delete(Long id) {
        findByIdOrThrow(id);
        patientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Patient findByIdOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Patient not found: " + id)
                        .asRuntimeException());
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Invalid date format, expected YYYY-MM-DD: " + date)
                    .asRuntimeException();
        }
    }
}