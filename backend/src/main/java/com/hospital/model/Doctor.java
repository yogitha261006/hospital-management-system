package com.hospital.model;

import com.hospital.enums.Gender;
import com.hospital.enums.StaffRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a doctor in the hospital, extending {@link Staff} with
 * specialization details and patient assignment tracking.
 *
 * <p>A doctor maintains a list of assigned patient IDs and prevents
 * duplicate assignments. All assignment changes are recorded in the
 * inherited audit log.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see Staff
 */
public class Doctor extends Staff {

    /** The doctor's medical specialization (e.g. "Cardiology", "Neurology"). */
    private String specialization;

    /** List of patient IDs currently assigned to this doctor. */
    private final List<String> assignedPatientIds;

    /**
     * Creates a new doctor with the specified details.
     *
     * <p>The doctor is automatically assigned the {@link StaffRole#DOCTOR} role.
     * An initial audit entry "DOCTOR_CREATED" is recorded.</p>
     *
     * @param name           full name of the doctor; must not be {@code null} or empty
     * @param age            age in years; must be greater than zero
     * @param gender         gender of the doctor; must not be {@code null}
     * @param contactNumber  contact phone number; must not be {@code null} or empty
     * @param specialization the medical specialization; must not be {@code null} or empty
     * @throws IllegalArgumentException if any validation constraint is violated
     */
    public Doctor(String name, int age, Gender gender, String contactNumber,
                  String specialization) {
        super(name, age, gender, contactNumber, StaffRole.DOCTOR);

        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization cannot be null or empty");
        }

        this.specialization = specialization;
        this.assignedPatientIds = new ArrayList<>();

        addAuditEntry("DOCTOR_CREATED",
                "Doctor '" + name + "' created with specialization: " + specialization);
    }

    /**
     * Returns the doctor's medical specialization.
     *
     * @return the specialization string; never {@code null}
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Assigns a patient to this doctor.
     *
     * <p>If the patient is already assigned, this method does nothing.
     * An audit entry is recorded for new assignments.</p>
     *
     * @param patientId the ID of the patient to assign; must not be {@code null}
     */
    public void assignPatient(String patientId) {
        if (patientId != null && !assignedPatientIds.contains(patientId)) {
            assignedPatientIds.add(patientId);
            addAuditEntry("PATIENT_ASSIGNED",
                    "Patient " + patientId + " assigned to Dr. " + getName());
        }
    }

    /**
     * Removes a patient assignment from this doctor.
     *
     * <p>An audit entry is recorded if the patient was previously assigned.</p>
     *
     * @param patientId the ID of the patient to remove
     */
    public void removePatient(String patientId) {
        if (assignedPatientIds.remove(patientId)) {
            addAuditEntry("PATIENT_REMOVED",
                    "Patient " + patientId + " removed from Dr. " + getName());
        }
    }

    /**
     * Returns an unmodifiable view of assigned patient IDs.
     *
     * @return an unmodifiable list of patient IDs; never {@code null}
     */
    public List<String> getAssignedPatientIds() {
        return Collections.unmodifiableList(assignedPatientIds);
    }

    /**
     * Returns the number of patients currently assigned to this doctor.
     *
     * @return the patient count
     */
    public int getPatientCount() {
        return assignedPatientIds.size();
    }

    /**
     * Returns a formatted string representation of this doctor.
     *
     * @return a string including specialization and patient count
     */
    @Override
    public String toString() {
        return String.format(
                "Doctor[id=%s, name='%s', specialization='%s', patients=%d, department=%s]",
                getStaffId(), getName(), specialization, getPatientCount(),
                getDepartmentId() != null ? getDepartmentId() : "unassigned");
    }
}
