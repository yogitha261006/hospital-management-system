package com.hospital.management.dto;

import com.hospital.management.entity.Patient;
import com.hospital.management.entity.PatientStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientDTO {
    private final Long patientId;
    private final String mrn;
    private final String name;
    private final LocalDate dateOfBirth;
    private final String ward;
    private final String bed;
    
    private final LocalDateTime admissionDate;
    private final LocalDateTime dischargeDate;
    private final PatientStatus currentStatus;
    private final Long assignedDoctorId;
    private final String assignedDoctorName;

    public PatientDTO(Patient patient) {
        this.patientId = patient.getPatientId();
        this.mrn = patient.getMrn();
        this.name = patient.getName();
        this.dateOfBirth = patient.getDateOfBirth();
        this.ward = patient.getWard();
        this.bed = patient.getBed();
        
        this.admissionDate = patient.getAdmissionDate();
        this.dischargeDate = patient.getDischargeDate();
        this.currentStatus = patient.getCurrentStatus();
        
        if (patient.getAssignedDoctor() != null) {
            this.assignedDoctorId = patient.getAssignedDoctor().getStaffId();
            this.assignedDoctorName = patient.getAssignedDoctor().getName();
        } else {
            this.assignedDoctorId = null;
            this.assignedDoctorName = null;
        }
    }

    public Long getPatientId() { return patientId; }
    public String getMrn() { return mrn; }
    public String getName() { return name; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getWard() { return ward; }
    public String getBed() { return bed; }
    
    public LocalDateTime getAdmissionDate() { return admissionDate; }
    public LocalDateTime getDischargeDate() { return dischargeDate; }
    public PatientStatus getCurrentStatus() { return currentStatus; }
    public Long getAssignedDoctorId() { return assignedDoctorId; }
    public String getAssignedDoctorName() { return assignedDoctorName; }
}
