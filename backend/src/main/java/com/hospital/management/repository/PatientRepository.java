package com.hospital.management.repository;

import com.hospital.management.entity.Patient;
import com.hospital.management.entity.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    long countByCurrentStatus(PatientStatus status);
    int countByAssignedDoctorStaffId(Long staffId);
}
