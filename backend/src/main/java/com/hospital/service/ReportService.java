package com.hospital.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hospital.enums.PatientStatus;
import com.hospital.exception.PatientNotFoundException;
import com.hospital.model.AuditEntry;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Hospital;
import com.hospital.model.Nurse;
import com.hospital.model.Patient;
import com.hospital.model.Ward;

/**
 * Read-only reporting service for the Hospital Management System.
 *
 * <p>{@code ReportService} aggregates data from the underlying {@link Hospital}
 * model to produce statistics, filtered lists, and comprehensive text reports.
 * It never exposes mutable state—all returned collections are either freshly
 * constructed or wrapped in unmodifiable views.</p>
 *
 * <p>Typical usage includes generating patient counts, staff breakdowns,
 * department-wise summaries, bed-availability snapshots, and a full-text
 * hospital report suitable for console output or logging.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see Hospital
 * @see Patient
 * @see Doctor
 * @see Nurse
 * @see Ward
 */
public class ReportService {

    /** The hospital instance this service reports on. */
    private final Hospital hospital;

    /**
     * Constructs a {@code ReportService} bound to the given hospital.
     *
     * @param hospital the {@link Hospital} instance to report on; must not be {@code null}
     */
    public ReportService(Hospital hospital) {
        this.hospital = hospital;
    }

    public Hospital getHospital() {
        return hospital;
    }

    /**
     * Returns the total number of patients registered in the hospital,
     * regardless of their current status.
     *
     * @return the total patient count
     */
    public int getTotalPatientCount() {
        return hospital.getAllPatients().size();
    }

    /**
     * Returns a list of patients who are currently admitted (status
     * {@code ADMITTED} or {@code UNDER_TREATMENT}).
     *
     * @return an unmodifiable list of currently admitted {@link Patient}s
     */
    public List<Patient> getAdmittedPatients() {
        return hospital.getAllPatients().stream()
                .filter(p -> p.getStatus() == PatientStatus.ADMITTED
                          || p.getStatus() == PatientStatus.UNDER_TREATMENT)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of patients who have been discharged.
     *
     * @return an unmodifiable list of discharged {@link Patient}s
     */
    public List<Patient> getDischargedPatients() {
        return hospital.getAllPatients().stream()
                .filter(p -> p.getStatus() == PatientStatus.DISCHARGED)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all doctors currently registered in the hospital.
     *
     * @return a list of {@link Doctor} instances
     */
    public List<Doctor> getAllDoctors() {
        return hospital.getAllStaff().stream()
                .filter(s -> s instanceof Doctor)
                .map(s -> (Doctor) s)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all nurses currently registered in the hospital.
     *
     * @return a list of {@link Nurse} instances
     */
    public List<Nurse> getAllNurses() {
        return hospital.getAllStaff().stream()
                .filter(s -> s instanceof Nurse)
                .map(s -> (Nurse) s)
                .collect(Collectors.toList());
    }

    /**
     * Returns an unmodifiable map of department names to their respective
     * staff counts.
     *
     * <p>The map preserves insertion order (department registration order).</p>
     *
     * @return an unmodifiable {@link Map} where keys are department names and
     *         values are the number of staff assigned to each department
     */
    public Map<String, Long> getDepartmentWiseStaffCount() {
        Map<String, Long> countMap = new LinkedHashMap<>();
        for (Department dept : hospital.getAllDepartments()) {
            countMap.put(dept.getDepartmentName(), (long) dept.getStaffCount());
        }
        return Collections.unmodifiableMap(countMap);
    }

    /**
     * Returns the total number of available (unoccupied) beds across all
     * wards in the hospital.
     *
     * @return the aggregate count of available beds
     */
    public int getTotalAvailableBeds() {
        return hospital.getAllWards().stream()
                .mapToInt(Ward::getAvailableBeds)
                .sum();
    }

    /**
     * Returns the audit (admission history) log for a specific patient.
     *
     * <p>The returned list is already unmodifiable as provided by the
     * {@link Patient#getAuditLog()} method.</p>
     *
     * @param patientId the unique identifier of the patient
     * @return an unmodifiable list of {@link AuditEntry} records
     * @throws PatientNotFoundException if no patient with the given ID exists
     */
    public List<AuditEntry> getPatientAdmissionHistory(String patientId)
            throws PatientNotFoundException {
        Patient patient = hospital.getPatient(patientId);
        return patient.getAuditLog(); // already unmodifiable
    }

    /**
     * Generates a comprehensive, human-readable text report of the hospital's
     * current state.
     *
     * <p>The report includes the following sections:</p>
     * <ul>
     *   <li><strong>Patient Statistics</strong> — total, admitted, and discharged counts</li>
     *   <li><strong>Staff Statistics</strong> — total staff, doctors, and nurses</li>
     *   <li><strong>Department-wise Staff Count</strong> — per-department breakdown</li>
     *   <li><strong>Bed Availability</strong> — per-ward and aggregate bed counts</li>
     *   <li><strong>Currently Admitted Patients</strong> — detailed per-patient entries</li>
     * </ul>
     *
     * @return a formatted {@link String} containing the full hospital report
     */
    public String generateFullReport() {
        StringBuilder sb = new StringBuilder();
        String separator = "═".repeat(60);

        sb.append("\n").append(separator);
        sb.append("\n         HOSPITAL MANAGEMENT SYSTEM — FULL REPORT");
        sb.append("\n         ").append(hospital.getHospitalName());
        sb.append("\n").append(separator);

        // Patient Statistics
        sb.append("\n\n📊 PATIENT STATISTICS");
        sb.append("\n────────────────────────────────────────");
        sb.append("\n  Total Patients       : ").append(getTotalPatientCount());
        sb.append("\n  Currently Admitted   : ").append(getAdmittedPatients().size());
        sb.append("\n  Discharged           : ").append(getDischargedPatients().size());

        // Staff Statistics
        sb.append("\n\n👨\u200D⚕️ STAFF STATISTICS");
        sb.append("\n────────────────────────────────────────");
        sb.append("\n  Total Staff          : ").append(hospital.getAllStaff().size());
        sb.append("\n  Doctors              : ").append(getAllDoctors().size());
        sb.append("\n  Nurses               : ").append(getAllNurses().size());

        // Department-wise
        sb.append("\n\n🏥 DEPARTMENT-WISE STAFF COUNT");
        sb.append("\n────────────────────────────────────────");
        Map<String, Long> deptCount = getDepartmentWiseStaffCount();
        if (deptCount.isEmpty()) {
            sb.append("\n  No departments registered.");
        } else {
            deptCount.forEach((dept, count) ->
                    sb.append(String.format("\n  %-20s : %d", dept, count)));
        }

        // Bed Availability
        sb.append("\n\n🛏️ BED AVAILABILITY");
        sb.append("\n────────────────────────────────────────");
        List<Ward> wards = hospital.getAllWards();
        if (wards.isEmpty()) {
            sb.append("\n  No wards registered.");
        } else {
            for (Ward w : wards) {
                sb.append(String.format("\n  %-15s : %d / %d available",
                        w.getWardName(), w.getAvailableBeds(), w.getTotalBeds()));
            }
            sb.append("\n  ──────────────────────");
            sb.append("\n  Total Available Beds : ").append(getTotalAvailableBeds());
        }

        // Admitted Patient Details
        sb.append("\n\n📋 CURRENTLY ADMITTED PATIENTS");
        sb.append("\n────────────────────────────────────────");
        List<Patient> admitted = getAdmittedPatients();
        if (admitted.isEmpty()) {
            sb.append("\n  No patients currently admitted.");
        } else {
            for (Patient p : admitted) {
                sb.append(String.format("\n  [%s] %s — Status: %s, Doctor: %s, Ward: %s",
                        p.getPatientId(), p.getName(), p.getStatus(),
                        p.getAssignedDoctorId() != null ? p.getAssignedDoctorId() : "Unassigned",
                        p.getAssignedWardId() != null ? p.getAssignedWardId() : "Unassigned"));
            }
        }

        sb.append("\n\n").append(separator);
        sb.append("\n         END OF REPORT");
        sb.append("\n").append(separator).append("\n");

        return sb.toString();
    }
}
