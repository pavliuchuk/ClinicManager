package com.testtask.hospital_system;

import com.testtask.hospital_system.model.Patient;
import com.testtask.hospital_system.model.PatientGender;
import com.testtask.hospital_system.repository.AgeStatRepository;
import com.testtask.hospital_system.repository.PatientHospitalRepository;
import com.testtask.hospital_system.repository.PatientRepository;
import com.testtask.hospital_system.service.PatientService;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientHospitalRepository patientHospitalRepository;

    @Autowired
    private AgeStatRepository ageStatRepository;

    @BeforeEach
    void cleanUp() {
        ageStatRepository.deleteAll();
        patientHospitalRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    void create_shouldPersistPatient() {
        Patient patient = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);

        assertThat(patient.getId()).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo("Max");
        assertThat(patient.getLastName()).isEqualTo("M");
        assertThat(patient.getDateOfBirth().toString()).isEqualTo("2000-02-20");
        assertThat(patient.getGender()).isEqualTo(PatientGender.MALE);
        assertThat(patientRepository.findById(patient.getId())).isPresent();
    }

    @Test
    void create_shouldThrowInvalidArgumentForBadDateFormat() {
        assertThatThrownBy(() -> patientService.create("Max", "M", "20-02-2000", PatientGender.MALE))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("INVALID_ARGUMENT");
    }

    @Test
    void update_shouldUpdateOnlyProvidedFields() {
        Patient patient = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);

        Patient updated = patientService.update(patient.getId(), "John", "", "", PatientGender.UNKNOWN);

        assertThat(updated.getFirstName()).isEqualTo("John");
        assertThat(updated.getLastName()).isEqualTo("M");
        assertThat(updated.getGender()).isEqualTo(PatientGender.MALE);
        assertThat(updated.getDateOfBirth().toString()).isEqualTo("2000-02-20");
    }

    @Test
    void update_shouldUpdateAllFields() {
        Patient patient = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);

        Patient updated = patientService.update(
                patient.getId(), "Maria", "S", "2000-08-20", PatientGender.FEMALE);

        assertThat(updated.getFirstName()).isEqualTo("Maria");
        assertThat(updated.getLastName()).isEqualTo("S");
        assertThat(updated.getDateOfBirth().toString()).isEqualTo("2000-08-20");
        assertThat(updated.getGender()).isEqualTo(PatientGender.FEMALE);
    }

    @Test
    void update_shouldThrowNotFoundForMissingPatient() {
        assertThatThrownBy(() -> patientService.update(9999L, "Jane", "", "", PatientGender.UNKNOWN))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void delete_shouldRemovePatient() {
        Patient patient = patientService.create("To Delete", "Patient", "1980-01-01", PatientGender.UNKNOWN);

        patientService.delete(patient.getId());

        assertThat(patientRepository.findById(patient.getId())).isEmpty();
    }

    @Test
    void delete_shouldThrowNotFoundForMissingPatient() {
        assertThatThrownBy(() -> patientService.delete(9999L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void findByIdOrThrow_shouldReturnPatient() {
        Patient patient = patientService.create("Alice", "B", "1990-03-20", PatientGender.FEMALE);

        Patient found = patientService.findByIdOrThrow(patient.getId());

        assertThat(found.getId()).isEqualTo(patient.getId());
        assertThat(found.getFirstName()).isEqualTo("Alice");
    }

    @Test
    void findByIdOrThrow_shouldThrowNotFoundForMissingPatient() {
        assertThatThrownBy(() -> patientService.findByIdOrThrow(9999L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }
}