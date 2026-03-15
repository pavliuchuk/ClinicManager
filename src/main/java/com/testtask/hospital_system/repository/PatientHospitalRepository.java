package com.testtask.hospital_system.repository;

import com.testtask.hospital_system.model.Hospital;
import com.testtask.hospital_system.model.Patient;
import com.testtask.hospital_system.model.PatientHospital;
import com.testtask.hospital_system.model.PatientHospitalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientHospitalRepository extends JpaRepository<PatientHospital, PatientHospitalId> {

    @Query("SELECT ph.patient FROM PatientHospital ph WHERE ph.hospital.id = :hospitalId")
    List<Patient> findPatientsByHospitalId(@Param("hospitalId") Long hospitalId);

    @Query("SELECT ph.hospital FROM PatientHospital ph WHERE ph.patient.id = :patientId")
    List<Hospital> findHospitalsByPatientId(@Param("patientId") Long patientId);

    @Modifying
    @Query("DELETE FROM PatientHospital ph WHERE ph.hospital.id = :hospitalId")
    void deleteByHospitalId(@Param("hospitalId") Long hospitalId);

    default boolean existsByPatientIdAndHospitalId(Long patientId, Long hospitalId) {
        return existsById(new PatientHospitalId(patientId, hospitalId));
    }
}