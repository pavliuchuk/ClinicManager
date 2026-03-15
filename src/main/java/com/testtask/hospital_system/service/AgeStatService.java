package com.testtask.hospital_system.service;

import com.testtask.hospital_system.model.AgeStatEntry;
import com.testtask.hospital_system.model.PatientGender;
import com.testtask.hospital_system.repository.AgeStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
public class AgeStatService {

    private final AgeStatRepository ageStatRepository;

    public AgeStatService(AgeStatRepository ageStatRepository) {
        this.ageStatRepository = ageStatRepository;
    }

    public void recordRegistration(Long hospitalId,
                                   LocalDate dateOfBirth,
                                   PatientGender gender,
                                   LocalDate registrationDate) {
        int age = Period.between(dateOfBirth, registrationDate).getYears();
        int year = registrationDate.getYear();
        int month = registrationDate.getMonthValue();

        AgeStatEntry entry = ageStatRepository
                .findByHospitalIdAndYearAndMonthAndGender(hospitalId, year, month, gender)
                .orElse(new AgeStatEntry(hospitalId, year, month, gender, 0, 0));

        entry.setTotalAge(entry.getTotalAge() + age);
        entry.setPatientCount(entry.getPatientCount() + 1);

        ageStatRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<AgeStatEntry> getAverageAgeStats(Long hospitalId) {
        int fromYear = LocalDate.now().getYear() - 10;
        return ageStatRepository.findByHospitalIdAndYearGreaterThanEqual(hospitalId, fromYear);
    }
}
