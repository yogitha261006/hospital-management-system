package com.hospital.model;

import com.hospital.enums.Gender;
import com.hospital.enums.StaffRole;

/**
 * Represents a nurse in the hospital, extending {@link Staff} with
 * shift scheduling and ward assignment capabilities.
 *
 * <p>A nurse operates on a specific shift type (DAY, NIGHT, or ROTATING)
 * and can be assigned to a particular ward. All changes are tracked
 * in the inherited audit log.</p>
 *
 * @author Hospital Management System
 * @version 1.0
 * @see Staff
 */
public class Nurse extends Staff {

    /** The shift type for this nurse: "DAY", "NIGHT", or "ROTATING". */
    private String shiftType;

    /** ID of the ward this nurse is assigned to; may be {@code null}. */
    private String assignedWardId;

    /**
     * Creates a new nurse with the specified details.
     *
     * <p>The nurse is automatically assigned the {@link StaffRole#NURSE} role.
     * An initial audit entry "NURSE_CREATED" is recorded.</p>
     *
     * @param name          full name of the nurse; must not be {@code null} or empty
     * @param age           age in years; must be greater than zero
     * @param gender        gender of the nurse; must not be {@code null}
     * @param contactNumber contact phone number; must not be {@code null} or empty
     * @param shiftType     the shift type ("DAY", "NIGHT", or "ROTATING");
     *                      must not be {@code null} or empty
     * @throws IllegalArgumentException if any validation constraint is violated
     */
    public Nurse(String name, int age, Gender gender, String contactNumber, String shiftType) {
        super(name, age, gender, contactNumber, StaffRole.NURSE);

        if (shiftType == null || shiftType.trim().isEmpty()) {
            throw new IllegalArgumentException("Shift type cannot be null or empty");
        }

        this.shiftType = shiftType;
        this.assignedWardId = null;

        addAuditEntry("NURSE_CREATED",
                "Nurse '" + name + "' created with shift type: " + shiftType);
    }

    /**
     * Returns the current shift type for this nurse.
     *
     * @return the shift type string ("DAY", "NIGHT", or "ROTATING"); never {@code null}
     */
    public String getShiftType() {
        return shiftType;
    }

    /**
     * Updates the shift type for this nurse.
     *
     * <p>An audit entry is recorded documenting the shift change.</p>
     *
     * @param shiftType the new shift type; must not be {@code null} or empty
     * @throws IllegalArgumentException if shiftType is null or empty
     */
    public void setShiftType(String shiftType) {
        if (shiftType == null || shiftType.trim().isEmpty()) {
            throw new IllegalArgumentException("Shift type cannot be null or empty");
        }
        String previousShift = this.shiftType;
        this.shiftType = shiftType;
        addAuditEntry("SHIFT_CHANGED",
                "Nurse '" + getName() + "' shift changed from " + previousShift
                        + " to " + shiftType);
    }

    /**
     * Returns the ID of the ward this nurse is assigned to.
     *
     * @return the ward ID, or {@code null} if not yet assigned
     */
    public String getAssignedWardId() {
        return assignedWardId;
    }

    /**
     * Assigns this nurse to a ward.
     *
     * <p>An audit entry is recorded documenting the ward assignment.</p>
     *
     * @param wardId the ID of the ward to assign to
     */
    public void assignWard(String wardId) {
        String previousWard = this.assignedWardId;
        this.assignedWardId = wardId;
        addAuditEntry("WARD_ASSIGNED",
                "Nurse '" + getName() + "' assigned to ward " + wardId
                        + (previousWard != null ? " (previously: " + previousWard + ")" : ""));
    }

    /**
     * Returns a formatted string representation of this nurse.
     *
     * @return a string including shift type and ward assignment
     */
    @Override
    public String toString() {
        return String.format(
                "Nurse[id=%s, name='%s', shift='%s', wardId=%s, department=%s]",
                getStaffId(), getName(), shiftType,
                assignedWardId != null ? assignedWardId : "unassigned",
                getDepartmentId() != null ? getDepartmentId() : "unassigned");
    }
}
