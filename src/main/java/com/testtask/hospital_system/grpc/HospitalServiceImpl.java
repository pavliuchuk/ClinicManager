package com.testtask.hospital_system.grpc;

import com.testtask.hospital_system.service.HospitalService;
import com.testtask.hospital_system.service.PatientService;
import com.testtask.hospital_system.service.RegistrationService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HospitalServiceImpl extends HospitalServiceGrpc.HospitalServiceImplBase {

    private final HospitalService hospitalService;
    private final PatientService patientService;
    private final RegistrationService registrationService;

    public HospitalServiceImpl(HospitalService hospitalService, PatientService patientService, RegistrationService registrationService) {
        this.hospitalService = hospitalService;
        this.patientService = patientService;
        this.registrationService = registrationService;
    }

    // Hospital

    @Override
    public void createHospital(CreateHospitalRequest request, StreamObserver<HospitalResponse> responseObserver) {
        var hospital = hospitalService.create(request.getName(), request.getAddress(), request.getCapacity());
        responseObserver.onNext(HospitalResponse.newBuilder().setHospital(Mapper.toProto(hospital)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateHospital(UpdateHospitalRequest request, StreamObserver<HospitalResponse> responseObserver) {
        var hospital = hospitalService.update(request.getId(), request.getName(), request.getAddress(), request.getCapacity());
        responseObserver.onNext(HospitalResponse.newBuilder().setHospital(Mapper.toProto(hospital)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteHospital(DeleteHospitalRequest request, StreamObserver<DeleteResponse> responseObserver) {
        hospitalService.delete(request.getId());
        responseObserver.onNext(DeleteResponse.newBuilder().setSuccess(true).setMessage("Hospital deleted").build());
        responseObserver.onCompleted();
    }

    // Patient

    @Override
    public void createPatient(CreatePatientRequest request, StreamObserver<PatientResponse> responseObserver) {
        var patient = patientService.create(
                request.getFirstName(), request.getLastName(),
                request.getDateOfBirth(), Mapper.toModelGender(request.getSex()));
        responseObserver.onNext(PatientResponse.newBuilder().setPatient(Mapper.toProto(patient)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updatePatient(UpdatePatientRequest request, StreamObserver<PatientResponse> responseObserver) {
        var patient = patientService.update(
                request.getId(), request.getFirstName(), request.getLastName(),
                request.getDateOfBirth(), Mapper.toModelGender(request.getSex()));
        responseObserver.onNext(PatientResponse.newBuilder().setPatient(Mapper.toProto(patient)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void deletePatient(DeletePatientRequest request, StreamObserver<DeleteResponse> responseObserver) {
        patientService.delete(request.getId());
        responseObserver.onNext(DeleteResponse.newBuilder().setSuccess(true).setMessage("Patient deleted").build());
        responseObserver.onCompleted();
    }

    // Registration

    @Override
    public void registerPatient(RegisterPatientRequest request, StreamObserver<RegistrationResponse> responseObserver) {
        boolean registered = registrationService.register(
                request.getPatientId(), request.getHospitalId(), request.getRegistrationDate());
        responseObserver.onNext(RegistrationResponse.newBuilder()
                .setPatientId(request.getPatientId())
                .setHospitalId(request.getHospitalId())
                .setStatus(registered ? "REGISTERED" : "ALREADY_REGISTERED")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void listPatientsInHospital(ListPatientsInHospitalRequest request, StreamObserver<ListPatientsResponse> responseObserver) {
        var patients = registrationService.listPatientsInHospital(request.getHospitalId())
                .stream().map(Mapper::toProto).toList();
        responseObserver.onNext(ListPatientsResponse.newBuilder().addAllPatients(patients).build());
        responseObserver.onCompleted();
    }

    @Override
    public void listHospitalsForPatient(ListHospitalsForPatientRequest request, StreamObserver<ListHospitalsResponse> responseObserver) {
        var hospitals = registrationService.listHospitalsForPatient(request.getPatientId())
                .stream().map(Mapper::toProto).toList();
        responseObserver.onNext(ListHospitalsResponse.newBuilder().addAllHospitals(hospitals).build());
        responseObserver.onCompleted();
    }
}