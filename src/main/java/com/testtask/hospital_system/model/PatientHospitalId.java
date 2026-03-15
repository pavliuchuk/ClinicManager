package com.testtask.hospital_system.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PatientHospitalId implements Serializable {

    private Long patientId;
    private Long hospitalId;

    public PatientHospitalId() {}

    public PatientHospitalId(Long patientId, Long hospitalId) {
        this.patientId = patientId;
        this.hospitalId = hospitalId;
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof PatientHospitalId other
                && Objects.equals(patientId, other.patientId)
                && Objects.equals(hospitalId, other.hospitalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, hospitalId);
    }
}