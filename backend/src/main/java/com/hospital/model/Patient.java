package com.hospital.model;

import com.hospital.enums.Gender;
import com.hospital.enums.PatientStatus;
import com.hospital.exception.AlreadyAdmittedException;
import com.hospital.exception.InvalidPatientStateException;
import com.hospital.exception.StaffAssignmentException;
import com.hospital.util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a patient in the hospital management system.
 *
 * <p>This is the most critical domain entity. It enforces full encapsulation with
 * no public fields and no direct state mutation. All state transitions are performed
 * through controlled methods that validate preconditions using the
 * {@link PatientStatus#canTransitionTo(PatientStatus)} guard.</p>
 *
 * <p>Every significant action is recorded in an immutable audit log, providing
 * a complete history of the patient's journey through the hospital system.</p>
 *
 * <h3>State Transition Diagram</h3>
 * <pre>
 *   REGISTERED → ADMITTED → UNDER_TREATMENT → DISCHARGED
 * </pre>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see PatientStatus
 * @see AuditEntry
 */
public class Patient {

    /** Formatter for displaying dates in string representations. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Unique patient identifier, auto-generated on creation. */
    private final String patientId;

    /** Medical Record Number, auto-generated on creation. */
    private final String medicalRecordNumber;

    /** Full name of the patient. */
    private String name;

    /** Age of the patient in years. */
    private int age;

    /** Gender of the patient. */
    private Gender gender;

    /** Contact phone number for the patient. */
    private String contactNumber;

    /** Residential address of the patient. */
    private String address;

    /** Current status of the patient in the hospital workflow. */
    private PatientStatus status;

    /** Staff ID of the doctor assigned to this patient; may be {@code null}. */
    private String assignedDoctorId;

    /** Ward ID this patient is assigned to; may be {@code null}. */
    private String assignedWardId;

    /** Timestamp when the patient was registered. */
    private LocalDateTime registrationDate;

    /** Timestamp when the patient was admitted; {@code null} if not yet admitted. */
    private LocalDateTime admissionDate;

    /** Timestamp when the patient was discharged; {@code null} if not yet discharged. */
    private LocalDateTime dischargeDate;

    /** Chronological log of all auditable actions related to this patient. */
    private final List<AuditEntry> auditLog;

    /**
     * Creates a new patient with the specified details.
     *
     * <p>The patient ID and medical record number are automatically generated.
     * The initial status is set to {@link PatientStatus#REGISTERED}, and the
     * registration date is set to the current timestamp.</p>
     *
     * @param name          full name of the patient; must not be {@code null} or empty
     * @param age           age in years; must be greater than zero
     * @param gender        gender of the patient; must not be {@code null}
     * @param contactNumber contact phone number; must not be {@code null} or empty
     * @param address       residential address; must not be {@code null} or empty
     * @throws IllegalArgumentException if any validation constraint is violated
     */
    public Patient(String name, int age, Gender gender, String contactNumber, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be null or empty");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Patient age must be greater than zero");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be null or empty");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }

        this.patientId = IdGenerator.generatePatientId();
        this.medicalRecordNumber = IdGenerator.generateMRN();
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.status = PatientStatus.REGISTERED;
        this.assignedDoctorId = null;
        this.assignedWardId = null;
        this.registrationDate = LocalDateTime.now();
        this.admissionDate = null;
        this.dischargeDate = null;
        this.auditLog = new ArrayList<>();

        addAuditEntry("PATIENT_REGISTERED",
                "Patient '" + name + "' registered with MRN: " + medicalRecordNumber);
    }

    // ========================================================================
    // Controlled State Transition Methods
    // ========================================================================

    /**
     * Admits the patient to the hospital.
     *
     * <p>Transitions the patient status from {@link PatientStatus#REGISTERED} to
     * {@link PatientStatus#ADMITTED}. Sets the admission date to the current timestamp.</p>
     *
     * @throws AlreadyAdmittedException    if the patient is already admitted or under treatment
     * @throws InvalidPatientStateException if the current status cannot transition to ADMITTED
     */
    public void admit() throws InvalidPatientStateException, AlreadyAdmittedException {
        if (status == PatientStatus.ADMITTED || status == PatientStatus.UNDER_TREATMENT) {
            throw new AlreadyAdmittedException(patientId);
        }
        if (!status.canTransitionTo(PatientStatus.ADMITTED)) {
            throw new InvalidPatientStateException(
                    "Cannot admit patient in status: " + status.getDisplayName());
        }
        this.status = PatientStatus.ADMITTED;
        this.admissionDate = LocalDateTime.now();
        addAuditEntry("PATIENT_ADMITTED",
                "Patient '" + name + "' admitted to hospital");
    }

    /**
     * Starts treatment for this patient.
     *
     * <p>Transitions the patient status from {@link PatientStatus#ADMITTED} to
     * {@link PatientStatus#UNDER_TREATMENT}.</p>
     *
     * @throws InvalidPatientStateException if the current status cannot transition to UNDER_TREATMENT
     */
    public void startTreatment() throws InvalidPatientStateException {
        if (!status.canTransitionTo(PatientStatus.UNDER_TREATMENT)) {
            throw new InvalidPatientStateException(
                    "Cannot start treatment for patient in status: " + status.getDisplayName());
        }
        this.status = PatientStatus.UNDER_TREATMENT;
        addAuditEntry("TREATMENT_STARTED",
                "Treatment started for patient '" + name + "'");
    }

    /**
     * Discharges the patient from the hospital.
     *
     * <p>Transitions the patient status to {@link PatientStatus#DISCHARGED} and
     * records the discharge date.</p>
     *
     * @throws InvalidPatientStateException if the current status cannot transition to DISCHARGED
     */
    public void discharge() throws InvalidPatientStateException {
        if (!status.canTransitionTo(PatientStatus.DISCHARGED)) {
            throw new InvalidPatientStateException(
                    "Cannot discharge patient in status: " + status.getDisplayName());
        }
        this.status = PatientStatus.DISCHARGED;
        this.dischargeDate = LocalDateTime.now();
        addAuditEntry("PATIENT_DISCHARGED",
                "Patient '" + name + "' discharged from hospital");
    }

    /**
     * Assigns a doctor to this patient.
     *
     * @param doctorId the staff ID of the doctor to assign; must not be {@code null} or empty
     * @throws InvalidPatientStateException if the patient has been discharged
     * @throws StaffAssignmentException     if the doctorId is null or empty
     */
    public void assignDoctor(String doctorId)
            throws InvalidPatientStateException, StaffAssignmentException {
        if (status == PatientStatus.DISCHARGED) {
            throw new InvalidPatientStateException(
                    "Cannot assign doctor to discharged patient");
        }
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new StaffAssignmentException(
                    "Doctor ID cannot be null or empty");
        }
        String previousDoctor = this.assignedDoctorId;
        this.assignedDoctorId = doctorId;
        addAuditEntry("DOCTOR_ASSIGNED",
                "Doctor " + doctorId + " assigned to patient '" + name + "'"
                        + (previousDoctor != null ? " (replacing: " + previousDoctor + ")" : ""));
    }

    /**
     * Assigns the patient to a ward.
     *
     * @param wardId the ward ID to assign to
     * @throws InvalidPatientStateException if the patient has been discharged
     */
    public void assignWard(String wardId) throws InvalidPatientStateException {
        if (status == PatientStatus.DISCHARGED) {
            throw new InvalidPatientStateException(
                    "Cannot assign ward to discharged patient");
        }
        String previousWard = this.assignedWardId;
        this.assignedWardId = wardId;
        addAuditEntry("WARD_ASSIGNED",
                "Patient '" + name + "' assigned to ward " + wardId
                        + (previousWard != null ? " (previously: " + previousWard + ")" : ""));
    }

    // ========================================================================
    // Getters
    // ========================================================================

    /**
     * Returns the unique patient ID.
     *
     * @return the patient ID; never {@code null}
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Returns the medical record number.
     *
     * @return the MRN; never {@code null}
     */
    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    /**
     * Returns the full name of the patient.
     *
     * @return the patient's name; never {@code null}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the age of the patient.
     *
     * @return the age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the gender of the patient.
     *
     * @return the gender; never {@code null}
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Returns the contact phone number.
     *
     * @return the contact number; never {@code null}
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Returns the residential address.
     *
     * @return the address; never {@code null}
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the current patient status.
     *
     * @return the patient status; never {@code null}
     */
    public PatientStatus getStatus() {
        return status;
    }

    /**
     * Returns the assigned doctor's staff ID.
     *
     * @return the doctor ID, or {@code null} if no doctor is assigned
     */
    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    /**
     * Returns the assigned ward ID.
     *
     * @return the ward ID, or {@code null} if no ward is assigned
     */
    public String getAssignedWardId() {
        return assignedWardId;
    }

    /**
     * Returns the registration date.
     *
     * @return the registration timestamp; never {@code null}
     */
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Returns the admission date.
     *
     * @return the admission timestamp, or {@code null} if not yet admitted
     */
    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    /**
     * Returns the discharge date.
     *
     * @return the discharge timestamp, or {@code null} if not yet discharged
     */
    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    /**
     * Returns an unmodifiable view of the patient's audit log.
     *
     * @return an unmodifiable list of audit entries; never {@code null}
     */
    public List<AuditEntry> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }

    // ========================================================================
    // Private Helpers
    // ========================================================================

    /**
     * Adds a new audit entry to this patient's audit log.
     *
     * @param action  short action label
     * @param details human-readable description
     */
    private void addAuditEntry(String action, String details) {
        auditLog.add(new AuditEntry(action, details));
    }

    // ========================================================================
    // String Representations
    // ========================================================================

    /**
     * Returns a concise summary of this patient.
     *
     * @return formatted patient summary string
     */
    @Override
    public String toString() {
        return String.format(
                "Patient[id=%s, mrn=%s, name='%s', age=%d, gender=%s, status=%s]",
                patientId, medicalRecordNumber, name, age,
                gender.getDisplayName(), status.getDisplayName());
    }

    /**
     * Returns a detailed, multi-line representation of this patient,
     * including all fields and the complete audit log.
     *
     * @return a comprehensive patient details string
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                      PATIENT DETAILS                       ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Patient ID       : %-40s ║%n", patientId));
        sb.append(String.format("║ MRN              : %-40s ║%n", medicalRecordNumber));
        sb.append(String.format("║ Name             : %-40s ║%n", name));
        sb.append(String.format("║ Age              : %-40d ║%n", age));
        sb.append(String.format("║ Gender           : %-40s ║%n", gender.getDisplayName()));
        sb.append(String.format("║ Contact          : %-40s ║%n", contactNumber));
        sb.append(String.format("║ Address          : %-40s ║%n", address));
        sb.append(String.format("║ Status           : %-40s ║%n", status.getDisplayName()));
        sb.append(String.format("║ Assigned Doctor  : %-40s ║%n",
                assignedDoctorId != null ? assignedDoctorId : "None"));
        sb.append(String.format("║ Assigned Ward    : %-40s ║%n",
                assignedWardId != null ? assignedWardId : "None"));
        sb.append(String.format("║ Registered       : %-40s ║%n",
                registrationDate.format(FORMATTER)));
        sb.append(String.format("║ Admitted         : %-40s ║%n",
                admissionDate != null ? admissionDate.format(FORMATTER) : "N/A"));
        sb.append(String.format("║ Discharged       : %-40s ║%n",
                dischargeDate != null ? dischargeDate.format(FORMATTER) : "N/A"));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║                       AUDIT LOG                            ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        for (AuditEntry entry : auditLog) {
            sb.append("║ ").append(entry.toString()).append("\n");
        }
        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
}
