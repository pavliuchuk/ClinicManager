package com.testtask.hospital_system;

import com.testtask.hospital_system.model.Hospital;
import com.testtask.hospital_system.repository.HospitalRepository;
import com.testtask.hospital_system.service.HospitalService;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class HospitalServiceTest {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @BeforeEach
    void cleanUp() {
        hospitalRepository.deleteAll();
    }

    @Test
    void create_shouldPersistHospital() {
        Hospital hospital = hospitalService.create("City Hospital", "123 Test St", 300);

        assertThat(hospital.getId()).isNotNull();
        assertThat(hospital.getName()).isEqualTo("City Hospital");
        assertThat(hospital.getAddress()).isEqualTo("123 Test St");
        assertThat(hospital.getCapacity()).isEqualTo(300);
        assertThat(hospitalRepository.findById(hospital.getId())).isPresent();
    }

    @Test
    void update_shouldUpdateOnlyProvidedFields() {
        Hospital hospital = hospitalService.create("Old Name", "Old Address", 100);

        Hospital updated = hospitalService.update(hospital.getId(), "New Name", "", 0);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getAddress()).isEqualTo("Old Address"); // unchanged
        assertThat(updated.getCapacity()).isEqualTo(100);          // unchanged
    }

    @Test
    void update_shouldUpdateAllFields() {
        Hospital hospital = hospitalService.create("Old Name", "Old Address", 100);

        Hospital updated = hospitalService.update(hospital.getId(), "New Name", "New Address", 500);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getAddress()).isEqualTo("New Address");
        assertThat(updated.getCapacity()).isEqualTo(500);
    }

    @Test
    void update_shouldThrowNotFoundForMissingHospital() {
        assertThatThrownBy(() -> hospitalService.update(9999L, "Name", "Address", 100))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void delete_shouldRemoveHospital() {
        Hospital hospital = hospitalService.create("To Delete", "Somewhere", 50);

        hospitalService.delete(hospital.getId());

        assertThat(hospitalRepository.findById(hospital.getId())).isEmpty();
    }

    @Test
    void delete_shouldThrowNotFoundForMissingHospital() {
        assertThatThrownBy(() -> hospitalService.delete(9999L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void findByIdOrThrow_shouldReturnHospital() {
        Hospital hospital = hospitalService.create("General", "456 Abc St", 200);

        Hospital found = hospitalService.findByIdOrThrow(hospital.getId());

        assertThat(found.getId()).isEqualTo(hospital.getId());
        assertThat(found.getName()).isEqualTo("General");
    }

    @Test
    void findByIdOrThrow_shouldThrowNotFoundForMissingHospital() {
        assertThatThrownBy(() -> hospitalService.findByIdOrThrow(9999L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }
}