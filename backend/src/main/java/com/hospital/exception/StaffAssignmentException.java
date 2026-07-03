package com.hospital.exception;

/**
 * Thrown when a staff assignment operation fails due to invalid
 * staff state, missing staff, or business rule violations.
 */
public class StaffAssignmentException extends HospitalException {

    public StaffAssignmentException(String message) {
        super(message);
    }

    public StaffAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
