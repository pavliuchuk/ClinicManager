# Clinic Manager

Clinic Manager is a backend service built to organize and link medical centers with their patients. Developed using Spring Boot and gRPC, this project focuses on efficient data handling and fast communication.

## Project Overview

This system serves for managing patients data. It allows users to track multiple clinics, register patients across different locations.

**Fast Communication**: Uses gRPC for low-latency, contract-first API design.

**Smart Storage**: Built with Hibernate and JPA to manage complex relationships between patients and facilities.

**In-Memory Speed**: Configured with an H2 database for rapid development and testing.

## Registration Design

The patient-hospital relationship is implemented as an explicit join entity `PatientHospital`.

The join entity uses a composite key (`PatientHospitalId`) consisting of `patientId + hospitalId`, which also naturally prevents a patient from being registered in the same hospital twice.

When a hospital is deleted, the registrations are explicitly cleaned up first via `deleteByHospitalId` before the hospital itself is removed, ensuring patients are never deleted as a side effect.

---

## Special Request - Average Age Statistics

### The Problem
With ~500,000 patients per year over 10 years, a live aggregation query would scan 5,000,000 rows on every request. This would never meet the <200ms requirement.

### Solution - Incremental Pre-aggregation
I introduced an `AgeStatEntry` entity that stores pre-aggregated results in buckets of `hospital / year / month / gender`. Every time a patient is registered, `RegistrationService` calls `AgeStatService.recordRegistration` which finds or creates the matching bucket and increments `totalAge` and `patientCount` by the patient's age at registration time.

The stats query then reads from the `age_stat` table filtered by `hospital_id` and `year >= now - 10`. The number of rows returned is naturally bounded by the number of distinct `year / month / gender` combinations, at most 360 (12 months × 10 years × 3 genders).

### Trade-offs
- Small write overhead on every registration - one extra write to `age_stat` to update the matching bucket
- If a patient's date of birth is corrected after registration, the stats are not automatically updated
- Deleting a registration would require decrementing the stats, which is not currently implemented (was not required in the task)

---

## Testing

### Integration Tests
All business logic is tested through integration tests. Tests are split by service layer:

- `HospitalServiceTest` - create, partial update, full update, delete, NOT_FOUND errors
- `PatientServiceTest` - same as above plus invalid date format validation
- `RegistrationServiceTest` - register, duplicate prevention, list patients/hospitals, empty lists, deleting hospital does not delete patient
- `AgeStatServiceTest` - bucket creation, bucket incrementing, separate buckets per gender/month, correct average age calculation, 10 year window filtering, duplicate registration not double counted and a response time assertion that verifies the query completes in under 200ms

### Manual Testing with Postman
The gRPC server runs on port 9090 with reflection enabled, so Postman auto-discovers all services without needing to import the proto file manually.

The full flow was tested in Postman:
1. `CreateHospital` → note hospital id
2. `CreatePatient` → note patient id
3. `RegisterPatient` → verify `"status": "REGISTERED"`
4. `DeleteHospital` → verify `"success": true`
5. `ListHospitalsForPatient` → returns `[]` confirming patient still exists but registration was cleaned up
