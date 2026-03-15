package com.testtask.hospital_system;

import com.testtask.hospital_system.model.AgeStatEntry;
import com.testtask.hospital_system.model.Hospital;
import com.testtask.hospital_system.model.PatientGender;
import com.testtask.hospital_system.repository.AgeStatRepository;
import com.testtask.hospital_system.repository.HospitalRepository;
import com.testtask.hospital_system.repository.PatientHospitalRepository;
import com.testtask.hospital_system.repository.PatientRepository;
import com.testtask.hospital_system.service.AgeStatService;
import com.testtask.hospital_system.service.HospitalService;
import com.testtask.hospital_system.service.PatientService;
import com.testtask.hospital_system.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AgeStatServiceTest {

    @Autowired
    private AgeStatService ageStatService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AgeStatRepository ageStatRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientHospitalRepository patientHospitalRepository;

    private Hospital hospital;

    @BeforeEach
    void setUp() {
        ageStatRepository.deleteAll();
        patientHospitalRepository.deleteAll();
        patientRepository.deleteAll();
        hospitalRepository.deleteAll();

        hospital = hospitalService.create("City Hospital", "123 Test St", 300);
    }

    @Test
    void recordRegistration_shouldCreateStatBucket() {
        var patient = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);

        registrationService.register(patient.getId(), hospital.getId(), "2024-01-15");

        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());
        assertThat(stats).hasSize(1);

        AgeStatEntry entry = stats.get(0);
        assertThat(entry.getYear()).isEqualTo(2024);
        assertThat(entry.getMonth()).isEqualTo(1);
        assertThat(entry.getGender()).isEqualTo(PatientGender.MALE);
        assertThat(entry.getPatientCount()).isEqualTo(1);
    }

    @Test
    void recordRegistration_shouldIncrementExistingBucket() {
        var patient1 = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);
        var patient2 = patientService.create("Bob", "B", "1998-06-10", PatientGender.MALE);

        registrationService.register(patient1.getId(), hospital.getId(), "2024-01-15");
        registrationService.register(patient2.getId(), hospital.getId(), "2024-01-20");

        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getPatientCount()).isEqualTo(2);
    }

    @Test
    void recordRegistration_shouldCreateSeparateBucketsForDifferentGender() {
        var male = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);
        var female = patientService.create("Maria", "S", "1995-06-15", PatientGender.FEMALE);

        registrationService.register(male.getId(), hospital.getId(), "2024-01-15");
        registrationService.register(female.getId(), hospital.getId(), "2024-01-20");

        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());

        assertThat(stats).hasSize(2);
        assertThat(stats).extracting(AgeStatEntry::getGender)
                .containsExactlyInAnyOrder(PatientGender.MALE, PatientGender.FEMALE);
    }

    @Test
    void recordRegistration_shouldCreateSeparateBucketsForDifferentMonths() {
        var patient1 = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);
        var patient2 = patientService.create("Bob", "B", "1998-06-10", PatientGender.MALE);

        registrationService.register(patient1.getId(), hospital.getId(), "2024-01-15");
        registrationService.register(patient2.getId(), hospital.getId(), "2024-02-15");

        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());

        assertThat(stats).hasSize(2);
        assertThat(stats).extracting(AgeStatEntry::getMonth)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void getAverageAgeStats_shouldCalculateCorrectAverageAge() {
        var patient1 = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);
        var patient2 = patientService.create("Bob", "B", "1994-01-15", PatientGender.MALE);

        registrationService.register(patient1.getId(), hospital.getId(), "2024-01-15");
        registrationService.register(patient2.getId(), hospital.getId(), "2024-01-15");

        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getAverageAge()).isEqualTo(26.5);
    }

    @Test
    void getAverageAgeStats_shouldOnlyReturnLast10Years() {
        var patient1 = patientService.create("Max", "M", "1970-02-20", PatientGender.MALE);
        var patient2 = patientService.create("Bob", "B", "1980-06-10", PatientGender.MALE);

        registrationService.register(patient1.getId(), hospital.getId(), "2024-01-15");

        ageStatRepository.save(new AgeStatEntry(hospital.getId(), 2010, 1, PatientGender.MALE, 40, 1));

        registrationService.register(patient2.getId(), hospital.getId(), "2024-02-15");

        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());

        assertThat(stats).extracting(AgeStatEntry::getYear)
                .allMatch(year -> year >= LocalDate.now().getYear() - 10);
    }

    @Test
    void getAverageAgeStats_shouldReturnEmptyForHospitalWithNoRegistrations() {
        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());

        assertThat(stats).isEmpty();
    }

    @Test
    void recordRegistration_shouldNotUpdateStatsOnDuplicateRegistration() {
        var patient = patientService.create("Max", "M", "2000-02-20", PatientGender.MALE);

        registrationService.register(patient.getId(), hospital.getId(), "2024-01-15");
        registrationService.register(patient.getId(), hospital.getId(), "2024-01-15");

        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getPatientCount()).isEqualTo(1);
    }

    @Test
    void getAverageAgeStats_shouldRespondUnder200ms() {
        for (int i = 0; i < 1000; i++) {
            var patient = patientService.create("First" + i, "L", "1990-01-01", PatientGender.MALE);
            registrationService.register(patient.getId(), hospital.getId(), "2025-03-15");
        }

        long start = System.currentTimeMillis();
        List<AgeStatEntry> stats = ageStatService.getAverageAgeStats(hospital.getId());
        long responseTime = System.currentTimeMillis() - start;

        assertThat(stats).isNotEmpty();
        assertThat(responseTime).isLessThan(200);
    }
}