package com.testtask.hospital_system.service;

import com.testtask.hospital_system.model.Hospital;
import com.testtask.hospital_system.model.Patient;
import com.testtask.hospital_system.model.PatientHospital;
import com.testtask.hospital_system.repository.PatientHospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RegistrationService {

    private final PatientHospitalRepository patientHospitalRepository;
    private final PatientService patientService;
    private final HospitalService hospitalService;
    private final AgeStatService ageStatService;

    public RegistrationService(PatientHospitalRepository patientHospitalRepository,
                               PatientService patientService,
                               HospitalService hospitalService,
                               AgeStatService ageStatService) {
        this.patientHospitalRepository = patientHospitalRepository;
        this.patientService = patientService;
        this.hospitalService = hospitalService;
        this.ageStatService = ageStatService;
    }

    /**
     * Returns true if newly registered, false if already registered.
     */
    public boolean register(Long patientId, Long hospitalId, String registrationDate) {
        if (patientHospitalRepository.existsByPatientIdAndHospitalId(patientId, hospitalId)) {
            return false;
        }

        Patient patient = patientService.findByIdOrThrow(patientId);
        Hospital hospital = hospitalService.findByIdOrThrow(hospitalId);

        LocalDate date = (registrationDate == null || registrationDate.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(registrationDate);

        patientHospitalRepository.save(new PatientHospital(patient, hospital, date));

        ageStatService.recordRegistration(hospitalId, patient.getDateOfBirth(), patient.getGender(), date);

        return true;
    }

    @Transactional(readOnly = true)
    public List<Patient> listPatientsInHospital(Long hospitalId) {
        hospitalService.findByIdOrThrow(hospitalId);
        return patientHospitalRepository.findPatientsByHospitalId(hospitalId);
    }

    @Transactional(readOnly = true)
    public List<Hospital> listHospitalsForPatient(Long patientId) {
        patientService.findByIdOrThrow(patientId);
        return patientHospitalRepository.findHospitalsByPatientId(patientId);
    }
}