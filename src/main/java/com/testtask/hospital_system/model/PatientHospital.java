package com.testtask.hospital_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patient_hospital")
public class PatientHospital {

    @EmbeddedId
    private PatientHospitalId id = new PatientHospitalId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("patientId")
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hospitalId")
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    public PatientHospital() {}

    public PatientHospital(Patient patient, Hospital hospital, LocalDate registrationDate) {
        this.patient = patient;
        this.hospital = hospital;
        this.registrationDate = registrationDate;
    }

    public PatientHospitalId getId() { return id; }
    public void setId(PatientHospitalId id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Hospital getHospital() { return hospital; }
    public void setHospital(Hospital hospital) { this.hospital = hospital; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}