package com.hospital.model;

import com.hospital.exception.DepartmentException;
import com.hospital.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a hospital department that manages a roster of staff members.
 *
 * <p>Each department has a unique auto-generated ID, a name, an optional head doctor,
 * and a list of staff member IDs. Staff additions and removals are validated to
 * prevent duplicates and invalid removals.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 */
public class Department {

    /** Unique identifier for this department, auto-generated on creation. */
    private final String departmentId;

    /** Human-readable name of the department (e.g. "Cardiology", "Emergency"). */
    private String departmentName;

    /** Staff ID of the head doctor for this department; may be {@code null}. */
    private String headDoctorId;

    /** List of staff member IDs assigned to this department. */
    private final List<String> staffIds;

    /**
     * Creates a new department with the specified name.
     *
     * <p>The department ID is automatically generated via
     * {@link IdGenerator#generateDepartmentId()}. The staff list is initialized
     * as empty, and no head doctor is assigned.</p>
     *
     * @param departmentName the name of the department; must not be {@code null} or empty
     * @throws IllegalArgumentException if departmentName is null or empty
     */
    public Department(String departmentName) {
        if (departmentName == null || departmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        this.departmentId = IdGenerator.generateDepartmentId();
        this.departmentName = departmentName;
        this.headDoctorId = null;
        this.staffIds = new ArrayList<>();
    }

    /**
     * Returns the unique department ID.
     *
     * @return the department ID; never {@code null}
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * Returns the name of this department.
     *
     * @return the department name; never {@code null}
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Returns the staff ID of the head doctor.
     *
     * @return the head doctor's staff ID, or {@code null} if none is assigned
     */
    public String getHeadDoctorId() {
        return headDoctorId;
    }

    /**
     * Adds a staff member to this department.
     *
     * @param staffId the staff ID to add; must not be {@code null}
     * @throws DepartmentException if the staff member is already in this department
     */
    public void addStaff(String staffId) throws DepartmentException {
        if (staffId == null || staffId.trim().isEmpty()) {
            throw new DepartmentException("Staff ID cannot be null or empty");
        }
        if (staffIds.contains(staffId)) {
            throw new DepartmentException(
                    "Staff member " + staffId + " is already assigned to department '"
                            + departmentName + "'");
        }
        staffIds.add(staffId);
    }

    /**
     * Removes a staff member from this department.
     *
     * @param staffId the staff ID to remove
     * @throws DepartmentException if the staff member is not in this department
     */
    public void removeStaff(String staffId) throws DepartmentException {
        if (!staffIds.remove(staffId)) {
            throw new DepartmentException(
                    "Staff member " + staffId + " is not assigned to department '"
                            + departmentName + "'");
        }
    }

    /**
     * Sets the head doctor for this department.
     *
     * @param doctorId the staff ID of the doctor to designate as head;
     *                 may be {@code null} to clear the assignment
     */
    public void setHeadDoctor(String doctorId) {
        this.headDoctorId = doctorId;
    }

    /**
     * Returns an unmodifiable view of the staff IDs in this department.
     *
     * @return an unmodifiable list of staff IDs; never {@code null}
     */
    public List<String> getStaffIds() {
        return Collections.unmodifiableList(staffIds);
    }

    /**
     * Returns the number of staff members in this department.
     *
     * @return the staff count
     */
    public int getStaffCount() {
        return staffIds.size();
    }

    /**
     * Returns a formatted string representation of this department.
     *
     * @return a string containing department details
     */
    @Override
    public String toString() {
        return String.format(
                "Department[id=%s, name='%s', headDoctor=%s, staffCount=%d]",
                departmentId, departmentName,
                headDoctorId != null ? headDoctorId : "none",
                staffIds.size());
    }
}
