package com.hospital.management.config;

import com.hospital.management.dto.CreatePatientRequest;
import com.hospital.management.dto.CreateStaffRequest;
import com.hospital.management.entity.StaffRole;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.StaffRepository;
import com.hospital.management.service.HospitalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DummyDataSeeder implements CommandLineRunner {

    private final HospitalService hospitalService;
    private final StaffRepository staffRepository;
    private final PatientRepository patientRepository;

    public DummyDataSeeder(HospitalService hospitalService, StaffRepository staffRepository, PatientRepository patientRepository) {
        this.hospitalService = hospitalService;
        this.staffRepository = staffRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (staffRepository.count() == 0 && patientRepository.count() == 0) {
            // 1. Create Staff
            CreateStaffRequest doc1 = new CreateStaffRequest();
            doc1.setEmployeeCode("EMP-D01");
            doc1.setName("Dr. Sarah Jenkins");
            doc1.setRole(StaffRole.DOCTOR);
            Long doc1Id = hospitalService.createStaff(doc1).getStaffId();
            hospitalService.setStaffAvailability(doc1Id, true);

            CreateStaffRequest doc2 = new CreateStaffRequest();
            doc2.setEmployeeCode("EMP-D02");
            doc2.setName("Dr. Marcus Thorne");
            doc2.setRole(StaffRole.DOCTOR);
            hospitalService.createStaff(doc2); // Keep unavailable for demo

            CreateStaffRequest nurse1 = new CreateStaffRequest();
            nurse1.setEmployeeCode("EMP-N01");
            nurse1.setName("Nurse Emily Clark");
            nurse1.setRole(StaffRole.NURSE);
            Long nurse1Id = hospitalService.createStaff(nurse1).getStaffId();
            hospitalService.setStaffAvailability(nurse1Id, true);

            // 2. Create Patients
            CreatePatientRequest p1 = new CreatePatientRequest();
            p1.setMrn("MRN-1001");
            p1.setName("John Doe");
            p1.setDateOfBirth(LocalDate.of(1985, 4, 12));
            Long p1Id = hospitalService.createPatient(p1).getPatientId();

            CreatePatientRequest p2 = new CreatePatientRequest();
            p2.setMrn("MRN-1002");
            p2.setName("Jane Smith");
            p2.setDateOfBirth(LocalDate.of(1990, 8, 25));
            Long p2Id = hospitalService.createPatient(p2).getPatientId();

            CreatePatientRequest p3 = new CreatePatientRequest();
            p3.setMrn("MRN-1003");
            p3.setName("Robert Johnson");
            p3.setDateOfBirth(LocalDate.of(1978, 11, 5));
            Long p3Id = hospitalService.createPatient(p3).getPatientId();

            // 3. Perform some state transitions for demo
            
            // Assign John Doe to Dr. Sarah and Admit him
            hospitalService.assignDoctorToPatient(p1Id, doc1Id);
            hospitalService.admitPatient(p1Id, "ICU", "Bed 4");

            // Assign Jane Smith to Nurse Emily and Admit her, then discharge her
            hospitalService.assignDoctorToPatient(p2Id, nurse1Id);
            hospitalService.admitPatient(p2Id, "General", "Bed 12");
            hospitalService.dischargePatient(p2Id);

            // Robert Johnson remains just registered
        }
    }
}
