package com.hospital.model;

import com.hospital.enums.Gender;
import com.hospital.enums.StaffRole;
import com.hospital.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for hospital staff members.
 *
 * <p>Uses composition for an immutable audit log that records all significant
 * actions performed on or by this staff member. Each staff member is assigned
 * a unique ID upon creation and can be associated with a department.</p>
 *
 * <p>Subclasses such as {@link Doctor} and {@link Nurse} extend this class
 * with role-specific behavior.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see Doctor
 * @see Nurse
 */
public class Staff {

    /** Unique identifier for this staff member, auto-generated on creation. */
    private final String staffId;

    /** Full name of the staff member. */
    private String name;

    /** Age of the staff member in years. */
    private int age;

    /** Gender of the staff member. */
    private Gender gender;

    /** Contact phone number for the staff member. */
    private String contactNumber;

    /** The role this staff member fulfills in the hospital. */
    private StaffRole role;

    /** ID of the department this staff member is assigned to; may be {@code null}. */
    private String departmentId;

    /** Chronological log of all auditable actions related to this staff member. */
    private final List<AuditEntry> auditLog;

    /**
     * Creates a new staff member with the specified details.
     *
     * <p>The staff ID is automatically generated via {@link IdGenerator#generateStaffId()}.
     * An initial audit entry "STAFF_CREATED" is recorded upon creation.</p>
     *
     * @param name          full name of the staff member; must not be {@code null} or empty
     * @param age           age in years; must be greater than zero
     * @param gender        gender of the staff member; must not be {@code null}
     * @param contactNumber contact phone number; must not be {@code null} or empty
     * @param role          the staff role; must not be {@code null}
     * @throws IllegalArgumentException if any validation constraint is violated
     */
    public Staff(String name, int age, Gender gender, String contactNumber, StaffRole role) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff name cannot be null or empty");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Staff age must be greater than zero");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Staff role cannot be null");
        }

        this.staffId = IdGenerator.generateStaffId();
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.role = role;
        this.departmentId = null;
        this.auditLog = new ArrayList<>();

        addAuditEntry("STAFF_CREATED",
                "Staff member '" + name + "' created with role " + role.getDisplayName());
    }

    /**
     * Returns the unique staff ID.
     *
     * @return the staff ID; never {@code null}
     */
    public String getStaffId() {
        return staffId;
    }

    /**
     * Returns the full name of this staff member.
     *
     * @return the staff member's name; never {@code null}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the age of this staff member.
     *
     * @return the age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the gender of this staff member.
     *
     * @return the gender; never {@code null}
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Returns the contact phone number of this staff member.
     *
     * @return the contact number; never {@code null}
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Returns the role of this staff member.
     *
     * @return the staff role; never {@code null}
     */
    public StaffRole getRole() {
        return role;
    }

    /**
     * Returns the department ID this staff member is assigned to.
     *
     * @return the department ID, or {@code null} if not yet assigned
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * Assigns this staff member to a department.
     *
     * <p>Records an audit entry documenting the assignment.</p>
     *
     * @param departmentId the ID of the department to assign to
     */
    public void assignDepartment(String departmentId) {
        String previousDept = this.departmentId;
        this.departmentId = departmentId;
        addAuditEntry("DEPARTMENT_ASSIGNED",
                "Staff '" + name + "' assigned to department " + departmentId
                        + (previousDept != null ? " (previously: " + previousDept + ")" : ""));
    }

    /**
     * Adds a new audit entry to this staff member's audit log.
     *
     * @param action  short action label (e.g. "STAFF_CREATED")
     * @param details human-readable description of the event
     */
    public void addAuditEntry(String action, String details) {
        auditLog.add(new AuditEntry(action, details));
    }

    /**
     * Returns an unmodifiable view of this staff member's audit log.
     *
     * @return an unmodifiable list of audit entries; never {@code null}
     */
    public List<AuditEntry> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }

    /**
     * Returns a formatted string representation of this staff member.
     *
     * @return a string containing all staff details
     */
    @Override
    public String toString() {
        return String.format(
                "Staff[id=%s, name='%s', age=%d, gender=%s, contact='%s', role=%s, departmentId=%s]",
                staffId, name, age, gender.getDisplayName(), contactNumber,
                role.getDisplayName(), departmentId != null ? departmentId : "unassigned");
    }
}
