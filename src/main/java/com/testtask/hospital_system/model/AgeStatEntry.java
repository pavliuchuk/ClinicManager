package com.testtask.hospital_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "age_stat",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hospital_id", "stat_year", "stat_month", "gender"}))
public class AgeStatEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "stat_year", nullable = false)
    private int year;

    @Column(name = "stat_month", nullable = false)
    private int month;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private PatientGender gender;

    @Column(name = "total_age", nullable = false)
    private long totalAge;

    @Column(name = "patient_count", nullable = false)
    private long patientCount;

    public AgeStatEntry() {}

    public AgeStatEntry(Long hospitalId, int year, int month, PatientGender gender, long totalAge, long patientCount) {
        this.hospitalId = hospitalId;
        this.year = year;
        this.month = month;
        this.gender = gender;
        this.totalAge = totalAge;
        this.patientCount = patientCount;
    }

    public double getAverageAge() {
        return patientCount == 0 ? 0 : (double) totalAge / patientCount;
    }

    public Long getId() { return id; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public PatientGender getGender() { return gender; }
    public void setGender(PatientGender gender) { this.gender = gender; }

    public long getTotalAge() { return totalAge; }
    public void setTotalAge(long totalAge) { this.totalAge = totalAge; }

    public long getPatientCount() { return patientCount; }
    public void setPatientCount(long patientCount) { this.patientCount = patientCount; }
}