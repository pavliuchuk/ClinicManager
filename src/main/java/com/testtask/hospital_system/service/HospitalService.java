package com.testtask.hospital_system.service;

import com.testtask.hospital_system.model.Hospital;
import com.testtask.hospital_system.repository.HospitalRepository;
import io.grpc.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public Hospital create(String name, String address, int capacity) {
        Hospital hospital = new Hospital(name, address, capacity);
        return hospitalRepository.save(hospital);
    }

    public Hospital update(Long id, String name, String address, int capacity) {
        Hospital hospital = findByIdOrThrow(id);

        if (!name.isBlank())    hospital.setName(name);
        if (!address.isBlank()) hospital.setAddress(address);
        if (capacity > 0)       hospital.setCapacity(capacity);

        return hospitalRepository.save(hospital);
    }

    public void delete(Long id) {
        findByIdOrThrow(id);
        hospitalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Hospital findByIdOrThrow(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> Status.NOT_FOUND
                        .withDescription("Hospital not found: " + id)
                        .asRuntimeException());
    }
}