package com.testtask.hospital_system;

import com.testtask.hospital_system.model.Hospital;
import com.testtask.hospital_system.model.Patient;
import com.testtask.hospital_system.model.PatientGender;
import com.testtask.hospital_system.model.PatientHospitalId;
import com.testtask.hospital_system.repository.HospitalRepository;
import com.testtask.hospital_system.repository.PatientHospitalRepository;
import com.testtask.hospital_system.repository.PatientRepository;
import com.testtask.hospital_system.service.HospitalService;
import com.testtask.hospital_system.service.PatientService;
import com.testtask.hospital_system.service.RegistrationService;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientHospitalRepository patientHospitalRepository;

    private Hospital hospital;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patientHospitalRepository.deleteAll();
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();

        hospital = hospitalService.create("City Hospital", "123 Test St", 300);
        patient = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);
    }

    @Test
    void register_shouldLinkPatientToHospital() {
        boolean result = registrationService.register(patient.getId(), hospital.getId(), "2024-01-15");

        assertThat(result).isTrue();
        assertThat(patientHospitalRepository.existsById(
                new PatientHospitalId(patient.getId(), hospital.getId()))).isTrue();
    }

    @Test
    void register_shouldReturnFalseOnDuplicateRegistration() {
        registrationService.register(patient.getId(), hospital.getId(), "2024-01-15");

        boolean duplicate = registrationService.register(patient.getId(), hospital.getId(), "2024-01-15");

        assertThat(duplicate).isFalse();
    }

    @Test
    void register_shouldUseCurrentDateWhenNotProvided() {
        boolean result = registrationService.register(patient.getId(), hospital.getId(), "");

        assertThat(result).isTrue();
    }

    @Test
    void register_shouldThrowNotFoundForMissingPatient() {
        assertThatThrownBy(() -> registrationService.register(9999L, hospital.getId(), ""))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void register_shouldThrowNotFoundForMissingHospital() {
        assertThatThrownBy(() -> registrationService.register(patient.getId(), 9999L, ""))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void listPatientsInHospital_shouldReturnAllRegisteredPatients() {
        Patient patient2 = patientService.create("Maria", "S", "1995-06-15", PatientGender.FEMALE);
        Patient patient3 = patientService.create("Bob", "B", "1988-03-22", PatientGender.MALE);

        registrationService.register(patient.getId(), hospital.getId(), "2024-01-01");
        registrationService.register(patient2.getId(), hospital.getId(), "2024-02-01");
        registrationService.register(patient3.getId(), hospital.getId(), "2024-03-01");

        List<Patient> patients = registrationService.listPatientsInHospital(hospital.getId());

        assertThat(patients).hasSize(3);
        assertThat(patients).extracting(Patient::getFirstName)
                .containsExactlyInAnyOrder("Max", "Maria", "Bob");
    }

    @Test
    void listPatientsInHospital_shouldReturnEmptyForHospitalWithNoPatients() {
        List<Patient> patients = registrationService.listPatientsInHospital(hospital.getId());

        assertThat(patients).isEmpty();
    }

    @Test
    void listPatientsInHospital_shouldThrowNotFoundForMissingHospital() {
        assertThatThrownBy(() -> registrationService.listPatientsInHospital(9999L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void listHospitalsForPatient_shouldReturnAllHospitals() {
        Hospital hospital2 = hospitalService.create("General Hospital", "456 Abc St", 150);
        Hospital hospital3 = hospitalService.create("North Medical", "789 Test Ave", 500);

        registrationService.register(patient.getId(), hospital.getId(), "2024-01-01");
        registrationService.register(patient.getId(), hospital2.getId(), "2024-02-01");
        registrationService.register(patient.getId(), hospital3.getId(), "2024-03-01");

        List<Hospital> hospitals = registrationService.listHospitalsForPatient(patient.getId());

        assertThat(hospitals).hasSize(3);
        assertThat(hospitals).extracting(Hospital::getName)
                .containsExactlyInAnyOrder("City Hospital", "General Hospital", "North Medical");
    }

    @Test
    void listHospitalsForPatient_shouldReturnEmptyForPatientWithNoRegistrations() {
        List<Hospital> hospitals = registrationService.listHospitalsForPatient(patient.getId());

        assertThat(hospitals).isEmpty();
    }

    @Test
    void listHospitalsForPatient_shouldThrowNotFoundForMissingPatient() {
        assertThatThrownBy(() -> registrationService.listHospitalsForPatient(9999L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void deleteHospital_shouldNotDeletePatient() {
        registrationService.register(patient.getId(), hospital.getId(), "2024-01-01");

        hospitalService.delete(hospital.getId());

        assertThat(patientRepository.findById(patient.getId())).isPresent();
    }
}