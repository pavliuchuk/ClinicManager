package com.testtask.hospital_system.service;

import com.testtask.hospital_system.model.PatientGender;
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

    public Patient create(String firstName, String lastName, String dateOfBirth, PatientGender gender) {
        Patient patient = new Patient(firstName, lastName, parseDate(dateOfBirth), gender);
        return patientRepository.save(patient);
    }

    public Patient update(Long id, String firstName, String lastName, String dateOfBirth, PatientGender gender) {
        Patient patient = findByIdOrThrow(id);

        if (!firstName.isEmpty())   patient.setFirstName(firstName);
        if (!lastName.isEmpty())    patient.setLastName(lastName);
        if (!dateOfBirth.isEmpty()) patient.setDateOfBirth(parseDate(dateOfBirth));
        if (gender != PatientGender.UNKNOWN) patient.setGender(gender);

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