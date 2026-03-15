package com.testtask.hospital_system.repository;

import com.testtask.hospital_system.model.AgeStatEntry;
import com.testtask.hospital_system.model.PatientGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgeStatRepository extends JpaRepository<AgeStatEntry, Long> {

    List<AgeStatEntry> findByHospitalIdAndYearGreaterThanEqual(Long hospitalId, int fromYear);

    Optional<AgeStatEntry> findByHospitalIdAndYearAndMonthAndGender(
            Long hospitalId, int year, int month, PatientGender gender);
}