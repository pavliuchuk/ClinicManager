package com.testtask.hospital_system.grpc;

import com.testtask.hospital_system.model.Hospital;
import com.testtask.hospital_system.model.Patient;
import com.testtask.hospital_system.model.PatientGender;

public class Mapper {

    private Mapper() {}

    // Hospital

    public static HospitalDto toProto(Hospital hospital) {
        return HospitalDto.newBuilder()
                .setId(hospital.getId())
                .setName(hospital.getName())
                .setAddress(hospital.getAddress())
                .setCapacity(hospital.getCapacity() != null ? hospital.getCapacity() : 0)
                .build();
    }

    // Patient

    public static PatientDto toProto(Patient patient) {
        return PatientDto.newBuilder()
                .setId(patient.getId())
                .setFirstName(patient.getFirstName())
                .setLastName(patient.getLastName())
                .setDateOfBirth(patient.getDateOfBirth().toString())
                .setSex(toProtoGender(patient.getGender()))
                .build();
    }

    // Gender

    public static Gender toProtoGender(PatientGender gender) {
        return switch (gender) {
            case MALE -> Gender.MALE;
            case FEMALE -> Gender.FEMALE;
            case OTHER -> Gender.OTHER;
            default -> Gender.UNKNOWN;
        };
    }

    public static PatientGender toModelGender(Gender gender) {
        return switch (gender) {
            case MALE -> PatientGender.MALE;
            case FEMALE -> PatientGender.FEMALE;
            case OTHER -> PatientGender.OTHER;
            default -> PatientGender.UNKNOWN;
        };
    }
}