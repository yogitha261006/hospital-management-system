package com.hospital.management.entity;

import com.hospital.management.exception.InvalidStateException;
import com.hospital.management.exception.PatientNotAdmittedException;
import com.hospital.management.exception.StaffUnavailableException;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    private String mrn;
    private String name;
    private LocalDate dateOfBirth;
    
    private String ward;
    private String bed;

    private LocalDateTime admissionDate;
    private LocalDateTime dischargeDate;

    @Enumerated(EnumType.STRING)
    private PatientStatus currentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_doctor_id")
    private Staff assignedDoctor;

    protected Patient() {
        // JPA requires no-arg constructor
    }

    public Patient(String mrn, String name, LocalDate dateOfBirth) {
        this.mrn = mrn;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.currentStatus = PatientStatus.REGISTERED; // Initial state
    }

    // Encapsulated Methods for Controlled State Transitions

    public void admit(String ward, String bed) {
        if (this.currentStatus != PatientStatus.REGISTERED) {
            throw new InvalidStateException("Only REGISTERED patients can be admitted.");
        }
        this.currentStatus = PatientStatus.ADMITTED;
        this.ward = ward;
        this.bed = bed;
        this.admissionDate = LocalDateTime.now();
        this.dischargeDate = null;
    }

    public void discharge() {
        if (this.currentStatus != PatientStatus.ADMITTED) {
            throw new PatientNotAdmittedException("Cannot discharge a patient who is not currently admitted.");
        }
        this.currentStatus = PatientStatus.DISCHARGED;
        this.dischargeDate = LocalDateTime.now();
        // optionally remove doctor assignment on discharge
        // this.assignedDoctor = null;
    }

    public void assignDoctor(Staff doctor) {
        if (doctor.getRole() != StaffRole.DOCTOR) {
            throw new InvalidStateException("Only staff with DOCTOR role can be assigned as a doctor.");
        }
        if (!doctor.isAvailable()) {
            throw new StaffUnavailableException("Cannot assign unavailable doctor: " + doctor.getName());
        }
        this.assignedDoctor = doctor;
    }

    // Helper method requested
    public void validateState() {
        if (currentStatus == PatientStatus.ADMITTED && admissionDate == null) {
            throw new InvalidStateException("Admitted patient must have an admission date.");
        }
        if (currentStatus == PatientStatus.DISCHARGED && dischargeDate == null && admissionDate != null) {
             // Just an example validation rule
            throw new InvalidStateException("Discharged patient must have a discharge date if they were admitted.");
        }
    }

    // Read-Only Accessors
    public Long getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public String getMrn() {
        return mrn;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getWard() {
        return ward;
    }

    public String getBed() {
        return bed;
    }

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    public PatientStatus getCurrentStatus() {
        return currentStatus;
    }

    public Staff getAssignedDoctor() {
        return assignedDoctor;
    }
}
