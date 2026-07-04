package com.hospital.management.service;

import com.hospital.management.dto.CreatePatientRequest;
import com.hospital.management.dto.CreateStaffRequest;
import com.hospital.management.dto.PatientDTO;
import com.hospital.management.dto.StaffDTO;
import com.hospital.management.entity.AuditLog;
import com.hospital.management.entity.Patient;
import com.hospital.management.entity.Staff;
import com.hospital.management.repository.AuditLogRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.StaffRepository;
import com.hospital.management.entity.PatientStatus;
import com.hospital.management.entity.StaffRole;
import com.hospital.management.dto.DashboardStatsDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalService {

    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final AuditLogRepository auditLogRepository;

    public HospitalService(PatientRepository patientRepository, StaffRepository staffRepository, AuditLogRepository auditLogRepository) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.auditLogRepository = auditLogRepository;
    }

    private void logAudit(String actor, String action, String details) {
        auditLogRepository.save(new AuditLog(actor, action, details));
    }

    // --- Patient Operations ---

    @Transactional
    public PatientDTO createPatient(CreatePatientRequest request) {
        // Enforce unique MRN
        if (patientRepository.existsByMrn(request.getMrn())) {
            throw new IllegalStateException("A patient with MRN " + request.getMrn() + " already exists.");
        }
        Patient patient = new Patient(
                request.getMrn(),
                request.getName(),
                request.getDateOfBirth()
        );
        patient = patientRepository.save(patient);
        logAudit("reception@hospital", "REGISTER_PATIENT", "MRN " + patient.getMrn() + ", " + patient.getName());
        return new PatientDTO(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        return new PatientDTO(getPatientEntity(id));
    }

    @Transactional
    public PatientDTO admitPatient(Long id, String ward, String bed) {
        Patient patient = getPatientEntity(id);
        patient.admit(ward, bed);
        patient.validateState();
        patient = patientRepository.save(patient);
        logAudit("reception@hospital", "ADMIT", "Ward " + ward + ", Bed " + bed);
        return new PatientDTO(patient);
    }

    @Transactional
    public PatientDTO dischargePatient(Long id) {
        Patient patient = getPatientEntity(id);
        patient.discharge();
        patient.validateState();
        patient = patientRepository.save(patient);
        logAudit("reception@hospital", "DISCHARGE", "Patient MRN " + patient.getMrn() + " discharged");
        return new PatientDTO(patient);
    }

    @Transactional
    public PatientDTO assignDoctorToPatient(Long patientId, Long staffId) {
        Patient patient = getPatientEntity(patientId);
        Staff doctor = staffRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id " + staffId));
        
        // Validate capacity
        int load = patientRepository.countByAssignedDoctorStaffId(staffId);
        int maxCapacity = doctor.getRole() == StaffRole.DOCTOR ? 12 : (doctor.getRole() == StaffRole.NURSE ? 8 : 0);
        if (load >= maxCapacity) {
            throw new IllegalStateException("Staff member has reached maximum capacity.");
        }

        patient.assignDoctor(doctor);
        patient = patientRepository.save(patient);
        logAudit("reception@hospital", "ASSIGN_STAFF", "Staff " + doctor.getName() + " assigned to MRN " + patient.getMrn());
        return new PatientDTO(patient);
    }

    // --- Staff Operations ---

    @Transactional
    public StaffDTO createStaff(CreateStaffRequest request) {
        Staff staff = new Staff(
                request.getEmployeeCode(),
                request.getName(),
                request.getRole()
        );
        staff = staffRepository.save(staff);
        logAudit("reception@hospital", "HIRE_STAFF", staff.getRole().name() + " " + staff.getName());
        return toStaffDTO(staff);
    }

    @Transactional(readOnly = true)
    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::toStaffDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StaffDTO setStaffAvailability(Long id, boolean isAvailable) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found with id " + id));
        if (isAvailable) {
            staff.markAvailable();
        } else {
            staff.markUnavailable();
        }
        staff = staffRepository.save(staff);
        logAudit("reception@hospital", isAvailable ? "STAFF_ON_DUTY" : "STAFF_OFF_DUTY", staff.getName());
        return toStaffDTO(staff);
    }

    private StaffDTO toStaffDTO(Staff staff) {
        StaffDTO dto = new StaffDTO(staff);
        dto.setLoad(patientRepository.countByAssignedDoctorStaffId(staff.getStaffId()));
        return dto;
    }

    // --- Dashboard Operations ---

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        long totalPatients = patientRepository.count();
        long admittedPatients = patientRepository.countByCurrentStatus(PatientStatus.ADMITTED);
        long dischargedPatients = patientRepository.countByCurrentStatus(PatientStatus.DISCHARGED);
        long availableStaff = staffRepository.countByAvailability(true);
        
        return new DashboardStatsDTO(totalPatients, admittedPatients, dischargedPatients, availableStaff);
    }

    // --- Audit Log Operations ---

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }

    // --- Helper Methods ---

    private Patient getPatientEntity(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id " + id));
    }
}
