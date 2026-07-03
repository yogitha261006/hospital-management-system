package com.hospital.model;

import com.hospital.exception.DepartmentException;
import com.hospital.exception.HospitalException;
import com.hospital.exception.PatientNotFoundException;
import com.hospital.exception.StaffAssignmentException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The aggregate root of the Hospital Management System.
 *
 * <p>Uses composition to hold and manage all domain entities: patients, staff members,
 * departments, and wards. All collections are stored internally as {@link LinkedHashMap}
 * instances to preserve insertion order, and all public accessors return defensive copies
 * or unmodifiable views to prevent external mutation of internal state.</p>
 *
 * <p>Each add/get operation performs validation and throws appropriate domain exceptions
 * when constraints are violated.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see Patient
 * @see Staff
 * @see Department
 * @see Ward
 */
public class Hospital {

    /** The name of this hospital. */
    private final String hospitalName;

    /** Map of patient ID → Patient, preserving insertion order. */
    private final Map<String, Patient> patients;

    /** Map of staff ID → Staff, preserving insertion order. */
    private final Map<String, Staff> staffMembers;

    /** Map of department ID → Department, preserving insertion order. */
    private final Map<String, Department> departments;

    /** Map of ward ID → Ward, preserving insertion order. */
    private final Map<String, Ward> wards;

    /**
     * Creates a new hospital with the specified name.
     *
     * <p>All internal entity collections are initialized as empty.</p>
     *
     * @param hospitalName the name of the hospital; must not be {@code null} or empty
     * @throws IllegalArgumentException if hospitalName is null or empty
     */
    public Hospital(String hospitalName) {
        if (hospitalName == null || hospitalName.trim().isEmpty()) {
            throw new IllegalArgumentException("Hospital name cannot be null or empty");
        }
        this.hospitalName = hospitalName;
        this.patients = new LinkedHashMap<>();
        this.staffMembers = new LinkedHashMap<>();
        this.departments = new LinkedHashMap<>();
        this.wards = new LinkedHashMap<>();
    }

    /**
     * Returns the name of this hospital.
     *
     * @return the hospital name; never {@code null}
     */
    public String getHospitalName() {
        return hospitalName;
    }

    // ========================================================================
    // Patient Operations
    // ========================================================================

    /**
     * Adds a patient to this hospital.
     *
     * @param patient the patient to add; must not be {@code null}
     * @throws HospitalException if the patient is null or a patient with the same ID already exists
     */
    public void addPatient(Patient patient) throws HospitalException {
        if (patient == null) {
            throw new HospitalException("Patient cannot be null");
        }
        if (patients.containsKey(patient.getPatientId())) {
            throw new HospitalException(
                    "Patient with ID " + patient.getPatientId() + " already exists");
        }
        patients.put(patient.getPatientId(), patient);
    }

    /**
     * Retrieves a patient by their ID.
     *
     * @param patientId the patient ID to look up
     * @return the patient with the specified ID; never {@code null}
     * @throws PatientNotFoundException if no patient is found with the given ID
     */
    public Patient getPatient(String patientId) throws PatientNotFoundException {
        Patient patient = patients.get(patientId);
        if (patient == null) {
            throw new PatientNotFoundException(patientId);
        }
        return patient;
    }

    /**
     * Returns an unmodifiable list of all patients in this hospital.
     *
     * <p>The returned list is a defensive copy; modifications to it will not
     * affect the hospital's internal state.</p>
     *
     * @return an unmodifiable list of all patients; never {@code null}
     */
    public List<Patient> getAllPatients() {
        return Collections.unmodifiableList(new ArrayList<>(patients.values()));
    }

    /**
     * Checks whether a patient with the given ID exists in this hospital.
     *
     * @param patientId the patient ID to check
     * @return {@code true} if the patient exists, {@code false} otherwise
     */
    public boolean hasPatient(String patientId) {
        return patients.containsKey(patientId);
    }

    // ========================================================================
    // Staff Operations
    // ========================================================================

    /**
     * Adds a staff member to this hospital.
     *
     * @param staff the staff member to add; must not be {@code null}
     * @throws HospitalException if the staff member is null or a staff member
     *                           with the same ID already exists
     */
    public void addStaff(Staff staff) throws HospitalException {
        if (staff == null) {
            throw new HospitalException("Staff member cannot be null");
        }
        if (staffMembers.containsKey(staff.getStaffId())) {
            throw new HospitalException(
                    "Staff member with ID " + staff.getStaffId() + " already exists");
        }
        staffMembers.put(staff.getStaffId(), staff);
    }

    /**
     * Retrieves a staff member by their ID.
     *
     * @param staffId the staff ID to look up
     * @return the staff member with the specified ID; never {@code null}
     * @throws StaffAssignmentException if no staff member is found with the given ID
     */
    public Staff getStaff(String staffId) throws StaffAssignmentException {
        Staff staff = staffMembers.get(staffId);
        if (staff == null) {
            throw new StaffAssignmentException(
                    "Staff member with ID " + staffId + " not found");
        }
        return staff;
    }

    /**
     * Returns an unmodifiable list of all staff members in this hospital.
     *
     * <p>The returned list is a defensive copy; modifications to it will not
     * affect the hospital's internal state.</p>
     *
     * @return an unmodifiable list of all staff members; never {@code null}
     */
    public List<Staff> getAllStaff() {
        return Collections.unmodifiableList(new ArrayList<>(staffMembers.values()));
    }

    /**
     * Checks whether a staff member with the given ID exists in this hospital.
     *
     * @param staffId the staff ID to check
     * @return {@code true} if the staff member exists, {@code false} otherwise
     */
    public boolean hasStaff(String staffId) {
        return staffMembers.containsKey(staffId);
    }

    // ========================================================================
    // Department Operations
    // ========================================================================

    /**
     * Adds a department to this hospital.
     *
     * @param department the department to add; must not be {@code null}
     * @throws HospitalException if the department is null or a department
     *                           with the same ID already exists
     */
    public void addDepartment(Department department) throws HospitalException {
        if (department == null) {
            throw new HospitalException("Department cannot be null");
        }
        if (departments.containsKey(department.getDepartmentId())) {
            throw new HospitalException(
                    "Department with ID " + department.getDepartmentId() + " already exists");
        }
        departments.put(department.getDepartmentId(), department);
    }

    /**
     * Retrieves a department by its ID.
     *
     * @param departmentId the department ID to look up
     * @return the department with the specified ID; never {@code null}
     * @throws DepartmentException if no department is found with the given ID
     */
    public Department getDepartment(String departmentId) throws DepartmentException {
        Department department = departments.get(departmentId);
        if (department == null) {
            throw new DepartmentException(
                    "Department with ID " + departmentId + " not found");
        }
        return department;
    }

    /**
     * Returns an unmodifiable list of all departments in this hospital.
     *
     * <p>The returned list is a defensive copy; modifications to it will not
     * affect the hospital's internal state.</p>
     *
     * @return an unmodifiable list of all departments; never {@code null}
     */
    public List<Department> getAllDepartments() {
        return Collections.unmodifiableList(new ArrayList<>(departments.values()));
    }

    // ========================================================================
    // Ward Operations
    // ========================================================================

    /**
     * Adds a ward to this hospital.
     *
     * @param ward the ward to add; must not be {@code null}
     * @throws HospitalException if the ward is null or a ward with the same ID already exists
     */
    public void addWard(Ward ward) throws HospitalException {
        if (ward == null) {
            throw new HospitalException("Ward cannot be null");
        }
        if (wards.containsKey(ward.getWardId())) {
            throw new HospitalException(
                    "Ward with ID " + ward.getWardId() + " already exists");
        }
        wards.put(ward.getWardId(), ward);
    }

    /**
     * Retrieves a ward by its ID.
     *
     * @param wardId the ward ID to look up
     * @return the ward with the specified ID; never {@code null}
     * @throws HospitalException if no ward is found with the given ID
     */
    public Ward getWard(String wardId) throws HospitalException {
        Ward ward = wards.get(wardId);
        if (ward == null) {
            throw new HospitalException(
                    "Ward with ID " + wardId + " not found");
        }
        return ward;
    }

    /**
     * Returns an unmodifiable list of all wards in this hospital.
     *
     * <p>The returned list is a defensive copy; modifications to it will not
     * affect the hospital's internal state.</p>
     *
     * @return an unmodifiable list of all wards; never {@code null}
     */
    public List<Ward> getAllWards() {
        return Collections.unmodifiableList(new ArrayList<>(wards.values()));
    }

    /**
     * Returns a formatted string representation of this hospital,
     * summarizing the counts of all managed entities.
     *
     * @return a string containing the hospital name and entity counts
     */
    @Override
    public String toString() {
        return String.format(
                "Hospital['%s'] — Patients: %d, Staff: %d, Departments: %d, Wards: %d",
                hospitalName, patients.size(), staffMembers.size(),
                departments.size(), wards.size());
    }
}
